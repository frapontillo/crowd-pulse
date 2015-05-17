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

/**
 * Advanced abstract class for {@link IPlugin<T>} that expose a method for processing single elements,
 * beyond regular {@link rx.Observable<T>} streams.
 *
 * This kind of class can be useful when writing a meta-implementation of an {@link IPlugin<T>} that may rely on one
 * or more different implementations.
 *
 * @author Francesco Pontillo
 */
public abstract class ISingleablePlugin<T> extends IPlugin<T> {

    /**
     * Process a single element according to the task of this {@link IPlugin<T>}.
     *
     * @param object The input element, of generic class {@link T}.
     * @return The processed input element, eventually modified.
     */
    public abstract T singleProcess(T object);

}
