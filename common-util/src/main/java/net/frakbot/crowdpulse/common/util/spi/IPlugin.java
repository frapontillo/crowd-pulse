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
public abstract class IPlugin<T> {

    /**
     * @return the name of the plugin implementation
     */
    public abstract String getName();

    /**
     * Returns the appropriate {@link rx.Observable.Operator} exposed by the plugin, which will work on a given
     * stream of data.
     *
     * @return An {@link rx.Observable.Operator} that works on {@link Observable}&lt;T&gt;.
     */
    protected abstract Observable.Operator<T, T> getOperator();

    /**
     * Default implementation to transform a stream of generic type {@link T} by applying the single operation provided
     * by {@link IPlugin#getOperator()} via {@link Observable#lift(Observable.Operator)}.
     * <p>
     *     If the {@link IPlugin<T>} doesn't use a single {@link rx.Observable.Operator}, you can override this method
     *     and provide your own transformation rules.
     * </p>
     *
     * @return A {@link rx.Observable.Transformer} that defines the proper transformations applied by this plugin
     * to the stream.
     */
    public Observable.Transformer<T, T> transform() {
        return tObservable -> tObservable.lift(getOperator());
    }

}
