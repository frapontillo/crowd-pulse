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

import rx.Observable;

/**
 * Simple interface for Plugins. Each plugin should declare a name so that it can be retrieved by the
 * {@link java.util.ServiceLoader}.
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
     *
     * @return An {@link rx.Observable.Operator} that works on {@link Observable<Input>} and emits values in a
     * {@link Observable<Output>}.
     */
    protected abstract Observable.Operator<Output, Input> getOperator(Parameter parameters);

    protected Observable.Operator<Output, Input> getOperator() {
        return getOperator(null);
    }

    /**
     * Default implementation to transform a stream of generic type {@link Output} by applying the single operation provided
     * by {@link IPlugin#getOperator()} via {@link Observable#lift(Observable.Operator)}.
     * <p>
     * If the {@link IPlugin< Output >} doesn't use a single {@link rx.Observable.Operator}, you can override this method
     * and provide your own transformation rules.
     *
     * @return A {@link rx.Observable.Transformer} that defines the proper transformations applied by this plugin
     * to the stream.
     */
    public Observable.Transformer<Input, Output> transform() {
        return inputObservable -> inputObservable.lift(getOperator());
    }

    /**
     * Default implementation that takes an {@link Observable< Output >} stream and transforms it applying the
     * {@link rx.Observable.Transformer} returned by {@link IPlugin#transform()}.
     * <p>
     * You should override this method only when the plugin generates a stream and when the
     * {@link IPlugin#process(Observable, Object)} method accepts {@code null} as valid input.
     *
     * @param stream The {@link Observable< Output >} to process.
     * @param params An optional parameter object of type {@link Parameter}.
     * @return A new {@link Observable< Output >} built by applying the {@link rx.Observable.Transformer} returned by
     * {@link IPlugin#transform()}.
     */
    public Observable<Output> process(Observable<Input> stream, Parameter params) {
        if (stream != null) {
            return stream.compose(this.transform());
        }
        return null;
    }

    /**
     * Process an {@link Observable< Output >} stream just as in {@link IPlugin#process(Observable, Object)} but with
     * {@code null} parameters.
     *
     * @param stream The {@link Observable< Output >} to process.
     * @return A new {@link Observable< Output >} built by applying the {@link rx.Observable.Transformer} returned by
     * {@link IPlugin#transform()}.
     * @see {@link IPlugin#process(Observable, Object)}
     */
    public Observable<Output> process(Observable<Input> stream) {
        return process(stream, null);
    }

}
