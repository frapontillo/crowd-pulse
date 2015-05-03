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

package net.frakbot.crowdpulse.playground.cli;

import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;
import net.frakbot.crowdpulse.common.util.rx.BackpressureAsyncTransformer;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.rx.BufferedMessageListObserver;
import net.frakbot.crowdpulse.sentiment.ISentimentAnalyzer;
import net.frakbot.crowdpulse.sentiment.sentit.SentitSentimentAnalyzer;
import net.frakbot.crowdpulse.sentiment.sentiwordnet.SentiWordNetSentimentAnalyzer;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class MessageSentimentMain {
    private SubscriptionGroupLatch allSubscriptions;
    private ISentimentAnalyzer analyzer;

    public static void main(String[] args) throws IOException {
        MessageSentimentMain main = new MessageSentimentMain();
        main.analyzer = new SentiWordNetSentimentAnalyzer();
        main.run(args);
    }

    public void run(String[] args) throws IOException {
        GenericAnalysisParameters params = MainHelper.start(args);
        final Observable<Message> candidates = MainHelper.getMessages(params);

        ConnectableObservable<Message> messages =
                analyzer.sentimentAnalyze(candidates)
                .compose(new BackpressureAsyncTransformer<>())
                .publish();

        Observable<List<Message>> bufferedMessages = messages.buffer(10, TimeUnit.SECONDS, 3);

        allSubscriptions = new SubscriptionGroupLatch(2);
        Subscription subscription = messages.subscribe(
                message -> MainHelper.getLogger().info("READ: \"{}\"", message.getText()),
                throwable -> allSubscriptions.countDown(),
                allSubscriptions::countDown);
        Subscription bufferedSubscription = bufferedMessages.subscribe(new BufferedMessageListObserver
                (allSubscriptions));
        allSubscriptions.setSubscriptions(subscription, bufferedSubscription);

        messages.connect();

        allSubscriptions.waitAllUnsubscribed();

        MainHelper.getLogger().info("Done.");
    }
}
