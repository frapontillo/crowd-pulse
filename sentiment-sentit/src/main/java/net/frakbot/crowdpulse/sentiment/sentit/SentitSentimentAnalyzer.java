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

package net.frakbot.crowdpulse.sentiment.sentit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.frakbot.crowdpulse.common.util.rx.Transformers;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.sentiment.ISentimentAnalyzer;
import net.frakbot.crowdpulse.sentiment.sentit.rest.*;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class SentitSentimentAnalyzer extends ISentimentAnalyzer {
    private final static String SENTIMENT_IMPL = "sentit";
    private final static String SENTIT_ENDPOINT = "http://sentit.cloudapp.net:9100/sentit/v2";
    private final int MAX_MESSAGES_PER_REQ = 10;
    private final SentitService service;

    public SentitSentimentAnalyzer() {
        // build the Gson deserializers collection
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SentitResponse.SentitResultMap.class, new SentitResultMapDeserializer())
                .create();
        // build the REST client
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SENTIT_ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new SentitInterceptor())
                .build();
        service = restAdapter.create(SentitService.class);
    }

    @Override public Observable<Message> sentimentAnalyze(Observable<Message> messages) {
        // split the messages into processable bundles (Sentit imposes limits on messages-per-request)
        Observable<List<Message>> bufferedMessages = messages.buffer(MAX_MESSAGES_PER_REQ);
        bufferedMessages = bufferedMessages.lift(new SentitOperator());
        // flatten the sequence of Observables back into one single Observable
        return bufferedMessages.compose(Transformers.flatten());
    }

    @Override public String getName() {
        return SENTIMENT_IMPL;
    }

    private class SentitOperator implements Observable.Operator<List<Message>, List<Message>> {
        @Override public Subscriber<? super List<Message>> call(Subscriber<? super List<Message>> subscriber) {
            return new SafeSubscriber<>(new Subscriber<List<Message>>() {
                @Override public void onCompleted() {
                    subscriber.onCompleted();
                }

                @Override public void onError(Throwable e) {
                    subscriber.onError(e);
                }

                @Override public void onNext(List<Message> messages) {
                    // make the request
                    SentitRequest request = new SentitRequest(messages);
                    SentitResponse response = service.classify(request);
                    // for each message, set the result
                    messages.forEach(message -> message.setSentiment(response.getSentimentForMessage(message)));
                    subscriber.onNext(messages);
                }
            });
        }
    }
}
