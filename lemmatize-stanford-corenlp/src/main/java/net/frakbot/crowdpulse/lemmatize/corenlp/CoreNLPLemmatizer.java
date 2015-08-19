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

package net.frakbot.crowdpulse.lemmatize.corenlp;

import edu.stanford.nlp.process.Morphology;
import net.frakbot.crowdpulse.common.util.spi.ISingleablePlugin;
import net.frakbot.crowdpulse.common.util.spi.VoidConfig;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.lemmatize.ILemmatizerOperator;
import rx.Observable;

import java.util.List;

/**
 * A {@link ISingleablePlugin} implementation that delegates the lemmatization process to the Stanford CoreNLP modules.
 * Stanford CoreNLP is only able to lemmatize some languages.
 * <p>
 * {@link Message}s that are marked as stop words won't be processed by this plugin.
 *
 * @author Francesco Pontillo
 */
public class CoreNLPLemmatizer extends ISingleablePlugin<Message, VoidConfig> {
    public final static String PLUGIN_NAME = "lemmatizer-stanford";
    private Morphology morphology;

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig getNewParameter() {
        return new VoidConfig();
    }

    @Override public Observable.Operator<Message, Message> getOperator(VoidConfig parameters) {
        CoreNLPLemmatizer currentLemmatizer = this;
        return new ILemmatizerOperator(this) {
            @Override public List<Token> lemmatizeMessageTokens(Message message) {
                return currentLemmatizer.singleItemProcess(message).getTokens();
            }
        };
    }

    @Override public Message singleItemProcess(Message message) {
        if (message.getTokens() != null) {
            message.getTokens().forEach(token -> {
                if (!token.isStopWord()) {
                    String lemma = getMorphology().lemma(token.getText(), token.getPos());
                    token.setLemma(lemma);
                }
            });
        }
        return message;
    }

    /**
     * Get the existing or build a CoreNLP {@link Morphology} if none exists.
     *
     * @return The CoreNLP {@link Morphology} instance.
     */
    private Morphology getMorphology() {
        if (morphology == null) {
            morphology = new Morphology();
        }
        return morphology;
    }
}
