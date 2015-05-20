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

package net.frakbot.crowdpulse.postagsimple.multi;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.ISingleablePlugin;
import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.postagsimple.ISimplePOSTaggerOperator;
import rx.Observable;

import java.util.List;

/**
 * Simple POS tagger that relies on external simple POS taggers based on the language they support.
 * For example, an English message will be tagged using the "simplepostagger-en" tagger, if any.
 *
 * @author Francesco Pontillo
 */
public class SimpleMultiPOSTagger extends IPlugin<Message, Message, Void> {
    private static final String SIMPLEPOSTAGGER_IMPL = "simplepostagger-multi";

    @Override public String getName() {
        return SIMPLEPOSTAGGER_IMPL;
    }

    @Override public Observable.Operator<Message, Message> getOperator(Void parameters) {
        SimpleMultiPOSTagger actualTagger = this;
        return new ISimplePOSTaggerOperator() {
            @Override public List<Token> posTagMessageTokens(Message message) {
                return actualTagger.simplePosTagMessageTokens(message);
            }
        };
    }

    private List<Token> simplePosTagMessageTokens(Message message) {
        if (message.getTokens() == null) {
            return null;
        }
        String language = message.getLanguage();
        IPlugin<Message, Message, Void> actualTagger = null;
        try {
            actualTagger = PluginProvider.getPlugin("simplepostagger-" + language);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (actualTagger != null && actualTagger instanceof ISingleablePlugin) {
            return ((ISingleablePlugin<Message, Void>) actualTagger).singleProcess(message).getTokens();
        }
        return message.getTokens();
    }
}
