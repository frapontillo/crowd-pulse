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
 * Simple abstract class, child of {@link IPlugin}, that exposes a method for processing single elements,
 * beyond regular {@link rx.Observable} streams.
 * <p>
 * This kind of class can be useful when writing a meta-implementation of an {@link IPlugin} that may rely on one
 * or more different implementations to delegate the actual calls to.
 * <p>
 * This "simpler" plugin emits objects of the same class as the input ones.
 *
 * @author Francesco Pontillo
 */
public abstract class ISingleablePlugin<InputOutput, Parameter> extends IPlugin<InputOutput, InputOutput, Parameter> {

    /**
     * Process a single element according to the task of this {@link IPlugin}.
     * This method may be re-used by actual implementations of {@link #getOperator(Parameter)}, or different
     * overrided methods.
     *
     * @param object The input element, of generic class {@link InputOutput}.
     * @return The processed input element, eventually modified.
     */
    public abstract InputOutput singleItemProcess(InputOutput object);

}
