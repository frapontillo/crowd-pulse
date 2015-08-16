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
 * Basic provider for CrowdPulse {@link IPlugin} implementations.
 * This provider simply retrieves the first {@link IPlugin} implementation whose name matches the asked one.
 * <p>
 * TODO: the provider can be improved by implementing a criteria based system.
 *
 * @author Francesco Pontillo
 */
public class PluginProvider {
    private final static ServiceLoader<IPlugin> serviceLoader;

    static {
        serviceLoader = ServiceLoader.load(IPlugin.class);
    }

    /**
     * Get an actual instance of {@link IPlugin} by looking for its name among the candidates.
     *
     * @param name        The name of the plugin to look for.
     * @param <Input>     The class of the elements that flow into the plugin.
     * @param <Output>    The class of the elements that flow out of the plugin.
     * @param <Parameter> The class of the parameter object that configures the plugin.
     * @param <Plugin>    The class of the returned plugin.
     * @return An instance of {@link Plugin}.
     * @throws ClassNotFoundException When no plugin implementation could be found for the specific name.
     */
    public static <Input, Output, Parameter extends IPluginConfig<Parameter>,
            Plugin extends IPlugin<Input, Output, Parameter>> Plugin getPlugin(String name)
            throws ClassNotFoundException {
        for (IPlugin implementation : serviceLoader) {
            if (implementation.getName().equals(name)) {
                return (Plugin) implementation;
            }
        }
        throw new ClassNotFoundException("Can't found a valid implementation for plugin named \"" + name + "\".");
    }
}
