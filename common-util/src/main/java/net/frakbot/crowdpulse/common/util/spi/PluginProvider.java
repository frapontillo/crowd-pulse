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

import java.util.ServiceLoader;

/**
 * @author Francesco Pontillo
 */
public class PluginProvider {
    private final static ServiceLoader<IPlugin> serviceLoader;

    static {
        serviceLoader = ServiceLoader.load(IPlugin.class);
    }

    public static <E, P, T extends IPlugin<E, P>> T getPlugin(String name) throws ClassNotFoundException {
        for (IPlugin implementation : serviceLoader) {
            // TODO: implement a better criteria-based loading logic
            if (implementation.getName().equals(name)) {
                return (T) implementation;
            }
        }
        throw new ClassNotFoundException("Can't found a valid implementation for plugin named \"" + name + "\"");
    }
}
