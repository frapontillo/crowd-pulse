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

package net.frakbot.crowdpulse.sentiment.spi;

import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import net.frakbot.crowdpulse.sentiment.ISentimentAnalyzer;

/**
 * @author Francesco Pontillo
 */
public class SentimentAnalyzerProvider extends PluginProvider<ISentimentAnalyzer> {
    private static SentimentAnalyzerProvider provider = new SentimentAnalyzerProvider(ISentimentAnalyzer.class);

    protected SentimentAnalyzerProvider(Class<ISentimentAnalyzer> clazz) {
        super(clazz);
    }

    public static ISentimentAnalyzer getPluginByName(String name) {
        return provider.getPlugin(name);
    }
}
