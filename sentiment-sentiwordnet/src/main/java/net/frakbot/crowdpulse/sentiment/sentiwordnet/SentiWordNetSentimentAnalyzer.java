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
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.sentiment.ISentimentAnalyzer;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observers.SafeSubscriber;

/**
 * @author Francesco Pontillo
 */
public class SentiWordNetSentimentAnalyzer extends ISentimentAnalyzer {
    private final static String SENTIMENT_IMPL = "sentiwordnet";
    private final MultiWordNet multiWordNet;
    private final SentiWordNet sentiWordNet;

    public SentiWordNetSentimentAnalyzer() {
        multiWordNet = new MultiWordNet();
        sentiWordNet = new SentiWordNet();
    }

    @Override public String getName() {
        return SENTIMENT_IMPL;
    }

    @Override public Observable<Message> sentimentAnalyze(Observable<Message> messages) {
        messages = messages.lift(new SimpleMessageOperator(this::processMessage));
        return messages;
    }

    private Message processMessage(Message message) {
        double totalScore = 0;
        for (Token token : message.getTokens()) {
            if (!StringUtil.isNullOrEmpty(token.getLemma())) {
                // retrieve and optionally filter the synsets according to WordNet POS tags
                String[] synsets = multiWordNet.getSynsets(token.getLemma(), message.getLanguage(), token.getSimplePos());
                // TODO: add configuration-specific weights for POS
                double synsetScore = sentiWordNet.getScore(synsets);
                totalScore += synsetScore;
                token.setScore(synsetScore);
            }
        }
        message.setSentiment(totalScore / message.getTokens().size());
        return message;
    }

    public class SimpleMessageOperator implements Observable.Operator<Message, Message> {
        private Func1<Message, Message> transformFn;

        public SimpleMessageOperator(Func1<Message, Message> transformFn) {
            this.transformFn = transformFn;
        }

        @Override public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
            return new SafeSubscriber<>(new Subscriber<Message>() {
                @Override public void onCompleted() {
                    subscriber.onCompleted();
                }

                @Override public void onError(Throwable e) {
                    subscriber.onError(e);
                }

                @Override public void onNext(Message message) {
                    subscriber.onNext(transformFn.call(message));
                }
            });
        }
    }

}
