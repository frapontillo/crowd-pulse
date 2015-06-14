/*
 * Copyright 2015 Francesco Pontillo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.frakbot.crowdpulse.common.util.spi;

import net.frakbot.crowdpulse.common.util.rx.BackpressureAsyncTransformer;
import rx.Observable;
import rx.Observer;
import rx.observables.ConnectableObservable;

import java.util.Arrays;

/**
 * Simple interface for Plugins.
 * <p>
 * A plugin should return an {@link rx.Observable.Operator}
 * <p>
 * Each plugin should also declare a name so that it can be retrieved by the {@link java.util.ServiceLoader}.
 *
 * @author Francesco Pontillo
 */
public abstract class IPlugin<Input, Output, Parameter> {

    /**
     * @return the name of the plugin implementation
     */
    public abstract String getName();

    /**
     * Returns the appropriate {@link rx.Observable.Operator<Input, Output>} exposed by the plugin, which will work
     * on a given stream of data of type {@code Input} and return a stream of type {@code Output}.
     *
     * @param parameters Plugin-specific parameters of class {@link Parameter} to invoke the operator.
     * @return An {@link rx.Observable.Operator} that works on {@link Observable<Input>} and emits values in a
     * {@link Observable<Output>}.
     */
    protected abstract Observable.Operator<Output, Input> getOperator(Parameter parameters);

    /**
     * @see IPlugin#getOperator(Object)
     */
    protected final Observable.Operator<Output, Input> getOperator() {
        return getOperator(null);
    }

    /**
     * Default implementation to transform a stream of generic type {@link Input} by applying the single operation
     * provided by {@link IPlugin#getOperator()} via {@link Observable#lift(Observable.Operator)}.
     * <p>
     * If the {@link IPlugin<Input>} doesn't use a single {@link rx.Observable.Operator}, you can override this method
     * and provide your own transformation rules. In this case, {@link IPlugin#getOperator()} should return {@code null}
     * and you have to override {@link IPlugin#transform(Object)}.
     *
     * @param params Parameters to perform the specific task with.
     * @return A {@link rx.Observable.Transformer} that defines the proper transformations applied by this plugin
     * to the stream.
     */
    public Observable.Transformer<Input, Output> transform(Parameter params) {
        Observable.Operator<Output, Input> operator = getOperator(params);
        if (operator != null) {
            return inputObservable -> inputObservable.lift(getOperator(params));
        }
        // if there is no operator, return null
        return null;
    }

    /**
     * @see IPlugin#transform(Object)
     */
    public final Observable.Transformer<Input, Output> transform() {
        return transform(null);
    }

    /**
     * Default implementation that takes an {@link Observable<Input>} stream and transforms it applying the
     * {@link rx.Observable.Transformer} returned by {@link IPlugin#transform(Object)}.
     * <p>
     * You should override this method only when the plugin generates a stream and when the
     * {@link IPlugin#processSingle(Object, Observable)} method accepts {@code null} as valid input.
     *
     * @param params An optional parameter object of type {@link Parameter}.
     * @param stream The {@link Observable<Input>} to process.
     * @return A new {@link Observable<Output>} built by applying the {@link rx.Observable.Transformer} returned by
     * {@link IPlugin#transform(Object)}.
     */
    public Observable<Output> processSingle(Parameter params, Observable<Input> stream) {
        if (stream != null) {
            return stream
                    .compose(this.transform(params))
                    .compose(new BackpressureAsyncTransformer<>());
        }
        return null;
    }

    /**
     * Process an {@link Observable<Input>} stream just as in {@link IPlugin#processSingle(Object, Observable)} but with
     * {@code null} parameters.
     *
     * @param stream The {@link Observable<Input>} to process.
     * @return A new {@link Observable<Output>} built by applying the {@link rx.Observable.Transformer} returned by
     * {@link IPlugin#transform(Object)}.
     * @see {@link IPlugin#processSingle(Object, Observable)}
     */
    public final Observable<Output> processSingle(Observable<Input> stream) {
        return processSingle(null, stream);
    }

    /**
     * Default implementation for processing multiple generic {@link Observable<Object>}s:
     * <ul>
     * <li>the first element is the one to process by {@link IPlugin#processSingle(Object, Observable)} and return</li>
     * <li>the remaining elements are the ones whose completion must be waited before streaming elements</li>
     * </ul>
     * <p>
     * Since all the "wait" streams are merged and subscribed on, you may want to make them cached via
     * {@link Observable#cache()} before passing them to the processing method.
     *
     * @param params  An optional parameter object of type {@link Parameter}.
     * @param streams The array of {@link Observable}s to use (only the first one will be processed).
     * @return A new {@link Observable<Output>} built by {@link IPlugin#processSingle(Object, Observable)}.
     */
    public Observable<Output> process(Parameter params, Observable<? extends Object>... streams) {
        if (streams.length == 1) {
            return processSingle(params, (Observable<Input>) streams[0]);
        }

        // the first element must be processed but must emit items
        streams[0] = streams[0].publish();

        Observable[] waitStreams = Arrays.copyOfRange(streams, 1, streams.length);
        Observable<Object> waitObservables = Observable.merge(waitStreams);
        waitObservables.subscribe(new Observer<Object>() {
            @Override public void onCompleted() {
                ((ConnectableObservable<Input>) streams[0]).connect();
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Object o) {
            }
        });

        return processSingle(params, (ConnectableObservable<Input>) streams[0]);
    }

    /**
     * Process an {@link Observable<Object>} stream array just as in {@link IPlugin#process(Object, Observable[])} but
     * with {@code null} parameters.
     *
     * @see {@link IPlugin#process(Object, Observable[])}
     */
    public final Observable<Output> process(Observable<? extends Object>... streams) {
        return process(null, streams);
    }

}
