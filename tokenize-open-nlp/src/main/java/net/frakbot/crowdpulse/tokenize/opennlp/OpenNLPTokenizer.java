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

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.VoidConfig;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.tokenize.ITokenizerOperator;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.logging.log4j.Logger;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Francesco Pontillo
 */
public class OpenNLPTokenizer extends IPlugin<Message, Message, VoidConfig> {
    public final static String PLUGIN_NAME = "tokenizer-opennlp";
    private Logger logger = CrowdLogger.getLogger(OpenNLPTokenizer.class);
    private Map<String, Tokenizer> tokenizers;
    private Set<String> unhandledLangs;

    public OpenNLPTokenizer() {
        tokenizers = new HashMap<>();
        unhandledLangs = new HashSet<>();
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig getNewParameter() {
        return new VoidConfig();
    }

    @Override protected Observable.Operator<Message, Message> getOperator(VoidConfig parameters) {
        return new ITokenizerOperator(this) {
            @Override public List<Token> getTokens(Message message) {
                Tokenizer tokenizer = getTokenizer(message.getLanguage());
                if (tokenizer == null) {
                    return null;
                }
                List<String> tokenList = Arrays.asList(tokenizer.tokenize(message.getText()));
                return tokenList.stream().map(Token::new).collect(Collectors.toList());
            }
        };
    }

    private Tokenizer getTokenizer(String language) {
        TokenizerModel model;
        Tokenizer tokenizer;
        // if the language could not be loaded before, don't try again
        if (unhandledLangs.contains(language)) {
            return null;
        }
        // attempt to load the tokenizer
        tokenizer = tokenizers.get(language);
        // if the tokenizer wasn't loaded before, load it
        if (tokenizer == null) {
            InputStream modelIn = null;
            try {
                // read the model and build the tokenizer
                modelIn = getClass().getClassLoader().getResourceAsStream(language + "-token.bin");
                model = new TokenizerModel(modelIn);
                tokenizer = new TokenizerME(model);
                tokenizers.put(language, tokenizer);
            } catch (IOException | IllegalArgumentException e) {
                unhandledLangs.add(language);
                logger.warn(String.format("There is no model for the language %s.", language));
            } finally {
                if (modelIn != null) {
                    try {
                        modelIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return tokenizer;
    }
}
