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

package net.frakbot.crowdpulse.social.spi;

import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import net.frakbot.crowdpulse.social.extraction.IExtractor;

/**
 * @author Francesco Pontillo
 */
public class ExtractorProvider extends PluginProvider<IExtractor> {
    private static ExtractorProvider provider = new ExtractorProvider(IExtractor.class);

    protected ExtractorProvider(Class<IExtractor> clazz) {
        super(clazz);
    }

    public static IExtractor getPluginByName(String name) {
        return provider.getPlugin(name);
    }
}
