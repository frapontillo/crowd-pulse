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
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.lemmatize.ILemmatizer;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class CoreNLPLemmatizer extends ILemmatizer {
    private final String LEMMATIZER_IMPL = "stanford";
    private final Morphology morphology;

    public CoreNLPLemmatizer() {
        morphology = new Morphology();
    }

    @Override public List<Token> lemmatizeMessageTokens(Message message) {
        if (message.getTokens() != null) {
            message.getTokens().forEach(token -> {
                if (!token.isStopWord()) {
                    String lemma = morphology.lemma(token.getText(), token.getPos());
                    token.setLemma(lemma);
                }
            });
        }
        return message.getTokens();
    }

    @Override public String getName() {
        return LEMMATIZER_IMPL;
    }
}