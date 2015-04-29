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
public abstract class PluginProvider<T extends IPlugin> {
    protected ServiceLoader<T> serviceLoader;

    protected PluginProvider(Class<T> clazz) {
        serviceLoader = ServiceLoader.load(clazz);
    }

    protected T getPlugin(String name) {
        for (T implementation : serviceLoader) {
            if (implementation.getName().equals(name)) {
                return implementation;
            }
        }
        return null;
    }
}
