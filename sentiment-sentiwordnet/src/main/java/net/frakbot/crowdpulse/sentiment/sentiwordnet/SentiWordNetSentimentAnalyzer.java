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

package net.frakbot.crowdpulse.sentiment.sentiwordnet;

import net.frakbot.crowdpulse.common.util.StringUtil;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.VoidConfig;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.sentiment.ISentimentAnalyzerOperator;
import rx.Observable;

import java.util.Map;

/**
 * Sentiment Analyzer based on MultiWordNet and SentiWordNet.
 * Each message is processed as follows:
 *
 * <ol>
 *     <li>Tokens are extracted.</li>
 *     <li>Each token has a lemma, a language and a generic simple POS tag ("n", "v", "a", "r"), so a collection
 *     of synsets is extracted from MultiWordNet according to those token features.</li>
 *     <li>For every synsets, sentiment scores are extracted from SentiWordNet (language-independent) and an average
 *     is calculated on those scores.</li>
 *     <li>When the sentiment analysis is done for every token, a sentiment average is computed on the whole token
 *     list.</li>
 * </ol>
 *
 * TODO: this algorithm can be improved in multiple ways:
 * TODO: abstract the logic of sentiment computation for each Token (multiple synsets/sentiments for each Token)
 * TODO: abstract the logic of sentiment computation for each Message (average of Token sentiment)
 * TODO: add configurations to the analyzer to weight some simple POS tags more than others
 *
 * @author Francesco Pontillo
 */
public class SentiWordNetSentimentAnalyzer extends IPlugin<Message, Message, VoidConfig> {
    public final static String PLUGIN_NAME = "sentiwordnet";
    private final MultiWordNet multiWordNet;
    private final SentiWordNet sentiWordNet;

    public SentiWordNetSentimentAnalyzer() {
        multiWordNet = new MultiWordNet();
        sentiWordNet = new SentiWordNet();
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig buildConfiguration(Map<String, String> configurationMap) {
        return new VoidConfig().buildFromMap(configurationMap);
    }

    @Override protected Observable.Operator<Message, Message> getOperator(VoidConfig parameters) {
        return new ISentimentAnalyzerOperator() {
            @Override public Message sentimentAnalyze(Message message) {
                double totalScore = 0;
                if (message.getTokens() == null) {
                    return message;
                }
                for (Token token : message.getTokens()) {
                    if (!StringUtil.isNullOrEmpty(token.getLemma())) {
                        // retrieve and optionally filter the synsets according to WordNet POS tags
                        String[] synsets = multiWordNet.getSynsets(token.getLemma(), message.getLanguage(), token.getSimplePos());
                        // TODO: add weights for simple POS according to some specific configuration
                        double synsetScore = sentiWordNet.getScore(synsets);
                        totalScore += synsetScore;
                        token.setScore(synsetScore);
                    }
                }
                message.setSentiment(totalScore / message.getTokens().size());
                return message;
            }
        };
    }

}
