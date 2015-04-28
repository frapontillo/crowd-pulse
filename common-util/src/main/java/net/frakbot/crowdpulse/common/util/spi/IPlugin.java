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
 * Simple interface for Plugins. Each plugin should declare a name so that it can be retrieved by the
 * {@link java.util.ServiceLoader}.
 *
 * @author Francesco Pontillo
 */
public interface IPlugin {
    /**
     * @return the name of the plugin implementation
     */
    String getName();
}
