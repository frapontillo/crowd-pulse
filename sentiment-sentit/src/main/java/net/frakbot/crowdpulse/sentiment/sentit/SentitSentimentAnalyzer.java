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
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.sentiment.sentit.rest.*;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class SentitSentimentAnalyzer extends IPlugin<Message, Message, Void> {
    private final static String SENTIMENT_IMPL = "sentit";
    private final static String SENTIT_ENDPOINT = "http://sentit.cloudapp.net:9100/sentit/v2";
    private final static int MAX_MESSAGES_PER_REQ = 10;
    private final static SentitService service;

    static {
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

    @Override public String getName() {
        return SENTIMENT_IMPL;
    }

    /**
     * This plugin doesn't give any {@link rx.Observable.Operator} as output, as it will only expose a custom
     * {@link rx.Observable.Transformer} that has to be applied to a stream of {@link Message}s.
     *
     * @return Always {@code null}.
     */
    @Override public Observable.Operator<Message, Message> getOperator(Void parameters) {
        return null;
    }

    @Override public Observable.Transformer<Message, Message> transform() {
        return messages -> messages
                .buffer(MAX_MESSAGES_PER_REQ)
                .lift(new SentitOperator())
                // flatten the sequence of Observables back into one single Observable
                .compose(Transformers.flatten());
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
                    SentitResponse response;
                    try {
                        response = service.classify(request);
                        // for each message, set the result
                        for (Message message : messages) {
                            message.setSentiment(response.getSentimentForMessage(message));
                        }
                    } catch (RetrofitError error) {
                        if (error.getResponse().getStatus() == 401) {
                            // TODO: implement some wait-until features (must be async!)
                        }
                    }
                    subscriber.onNext(messages);
                }
            });
        }
    }
}
