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

package net.frakbot.crowdpulse.tokenize.opennlp;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.tokenize.ITokenizerOperator;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Francesco Pontillo
 */
public class OpenNLPTokenizer extends IPlugin<Message, Void> {
    private final static String TOKENIZER_IMPL = "opennlp";
    private Map<String, TokenizerModel> models;

    public OpenNLPTokenizer() {
        models = new HashMap<>();
    }

    @Override public String getName() {
        return TOKENIZER_IMPL;
    }

    @Override protected Observable.Operator<Message, Message> getOperator() {
        return new ITokenizerOperator() {
            @Override public List<Token> getTokens(Message message) {
                TokenizerModel tokenizerModel = getModel(message.getLanguage());
                if (tokenizerModel == null) {
                    return null;
                }
                Tokenizer tokenizer = new TokenizerME(tokenizerModel);
                List<String> tokenList = Arrays.asList(tokenizer.tokenize(message.getText()));
                return tokenList.stream().map(Token::new).collect(Collectors.toList());
            }
        };
    }

    private TokenizerModel getModel(String language) {
        TokenizerModel model;
        if ((model = models.get(language)) == null) {
            InputStream modelIn = null;
            try {
                modelIn = getClass().getClassLoader().getResourceAsStream(language + "-token.bin");
                model = new TokenizerModel(modelIn);
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
