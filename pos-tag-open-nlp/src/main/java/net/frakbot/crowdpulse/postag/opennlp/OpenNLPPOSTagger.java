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

package net.frakbot.crowdpulse.postag.opennlp;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.postag.IPOSTaggerOperator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Francesco Pontillo
 */
public class OpenNLPPOSTagger extends IPlugin<Message, Void> {
    private final static String POSTAGGER_IMPL = "opennlp";
    private Map<String, POSModel> models;

    public OpenNLPPOSTagger() {
        models = new HashMap<>();
    }

    @Override public String getName() {
        return POSTAGGER_IMPL;
    }

    @Override public Observable.Operator<Message, Message> getOperator() {
        return new IPOSTaggerOperator() {
            @Override public List<Token> posTagMessageTokens(Message message) {
                if (message.getTokens() == null) {
                    return null;
                }
                POSModel posModel = getModel(message.getLanguage());
                if (posModel == null) {
                    return null;
                }
                POSTaggerME posTagger = new POSTaggerME(posModel);

                // transform the List of Tokens to an Array of Strings
                List<String> posTagsList = message.getTokens().stream().map(Token::getText).collect(Collectors.toList());
                String[] posTags = posTagsList.toArray(new String[posTagsList.size()]);
                // fire up the POS-tagging, get the Token POS tags
                posTags = posTagger.tag(posTags);

                // associate each POS with the corresponding Token
                for (int i = 0; i < message.getTokens().size(); i++) {
                    message.getTokens().get(i).setPos(posTags[i]);
                }

                return message.getTokens();
            }
        };
    }

    private POSModel getModel(String language) {
        POSModel model;
        if ((model = models.get(language)) == null) {
            InputStream modelIn = null;
            try {
                modelIn = getClass().getClassLoader().getResourceAsStream(language + "-pos-maxent.bin");
                model = new POSModel(modelIn);
                models.put(language, model);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException ignored) {
            } finally {
                if (modelIn != null) {
                    try {
                        modelIn.close();
                    } catch (IOException ignored) { }
                }
            }
        }
        return model;
    }
}
