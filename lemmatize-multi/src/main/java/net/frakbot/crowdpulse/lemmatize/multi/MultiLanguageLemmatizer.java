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

package net.frakbot.crowdpulse.lemmatize.multi;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.ISingleablePlugin;
import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import net.frakbot.crowdpulse.common.util.spi.VoidConfig;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.lemmatize.ILemmatizerOperator;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A multi-language implementation for {@link IPlugin<Message>}.
 *
 * When a {@link Message} goes through the lemmatization process, the concrete implementation is searched for in the
 * following (ordered) locations:
 *
 * <ol>
 *     <li>In the internal {@link HashMap} {@link MultiLanguageLemmatizer#lemmatizerMap}, starting from the language</li>
 *     <li>In any external SPI-enabled implementation with the name format as `lemmatizer-LANG`</li>
 *     <li>Finally, the Stanford-CoreNLP implementation is used if no other is found (note that Stanford-CoreNLP may
 *     not support every language)</li>
 * </ol>
 *
 * A class may override {@link MultiLanguageLemmatizer} in order to provide some different default implementation
 * (in stead of Stanford-CoreNLP) or in order to hardcode language-specific implementation in the internal
 * {@link MultiLanguageLemmatizer#lemmatizerMap}.
 *
 * @author Francesco Pontillo
 */
public class MultiLanguageLemmatizer extends IPlugin<Message, Message, VoidConfig> {
    public final static String PLUGIN_NAME = "lemmatizer-multi";
    private final static String LEMMATIZER_WILDCARD = "*";
    private final HashMap<String, String> lemmatizerMap;
    private final HashMap<String, IPlugin<Message, Message, VoidConfig>> lemmatizers;

    public MultiLanguageLemmatizer() {
        lemmatizerMap = new HashMap<>();
        // all lemmatizers must follow the "lemmatizer-LANG" format
        // e.g. Italian language will use "lemmatizer-it", French will use "lemmatizer-fr"

        // add here every other lemmatizer that doesn't follow the "lemmatizer-LANG" format in its name
        // e.g. lemmatizerMap.put("de", "my-custom-german-lemmatizer")

        // if no implementation is found for a given language, fallback to Stanford-CoreNLP
        // (some languages may not be supported)
        lemmatizerMap.put(LEMMATIZER_WILDCARD, "lemmatizer-stanford");

        lemmatizers = new HashMap<>();
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig buildConfiguration(Map<String, String> configurationMap) {
        return new VoidConfig().buildFromMap(configurationMap);
    }

    @Override public Observable.Operator<Message, Message> getOperator(VoidConfig parameters) {
        return new ILemmatizerOperator() {
            @Override public List<Token> lemmatizeMessageTokens(Message message) {
                // find or instantiate the lemmatizer
                IPlugin<Message, Message, VoidConfig> lemmatizer = getLemmatizerForMessage(message);
                if (lemmatizer instanceof ISingleablePlugin) {
                    return ((ISingleablePlugin<Message, VoidConfig>) lemmatizer).singleItemProcess(message).getTokens();
                }
                return null;
            }
        };
    }

    private IPlugin<Message, Message, VoidConfig> getLemmatizerForMessage(Message message) {
        IPlugin<Message, Message, VoidConfig> lemmatizer;
        String lang = message.getLanguage();
        // find or instantiate the lemmatizer
        if ((lemmatizer = lemmatizers.get(lang)) == null) {
            // look for the specific implementation in the map
            String lemmatizerIdentifier = lemmatizerMap.get(lang);
            // if there is no implementation, use the default "lemmatizer-LANG" format
            if (lemmatizerIdentifier == null) {
                lemmatizerIdentifier = "lemmatizer-" + lang;
            }
            // look for the lemmatizer implementation in the SPI
            try {
                lemmatizer = PluginProvider.getPlugin(lemmatizerIdentifier);
            } catch (ClassNotFoundException ignored) {}
            // if the lemmatizer isn't provided by the SPI, use the one provided for every language, as "*"
            if (lemmatizer == null) {
                try {
                    lemmatizer = PluginProvider.getPlugin(lemmatizerMap.get(LEMMATIZER_WILDCARD));
                } catch (ClassNotFoundException ignored) {}
            }
            lemmatizers.put(lang, lemmatizer);
        }
        return lemmatizer;
    }
}
