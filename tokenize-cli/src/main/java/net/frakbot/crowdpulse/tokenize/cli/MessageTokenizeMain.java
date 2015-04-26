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

package net.frakbot.crowdpulse.tokenize.cli;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;
import net.frakbot.crowdpulse.common.util.rx.BackpressureAsyncTransformer;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import net.frakbot.crowdpulse.data.rx.BufferedMessageListObserver;
import net.frakbot.crowdpulse.tokenize.ITokenizer;
import net.frakbot.crowdpulse.tokenize.opennlp.OpenNLPTokenizer;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class MessageTokenizeMain {
    private SubscriptionGroupLatch allSubscriptions;
    private final Logger logger = CrowdLogger.getLogger(MessageTokenizeMain.class);
    private ITokenizer tokenizer;

    public static void main(String[] args) throws IOException {
        MessageTokenizeMain main = new MessageTokenizeMain();
        main.tokenizer = new OpenNLPTokenizer();
        main.run(args);
    }

    public void run(String[] args) throws IOException {
        logger.info("Message tokenization started.");

        // read parameters
        GenericAnalysisParameters params = new GenericAnalysisParameters();
        new JCommander(params, args);
        logger.info("Parameters read.");

        MessageRepository messageRepository = new MessageRepository();
        final Observable<Message> candidates = messageRepository.getBetweenIdsAsObservable(
                params.getFrom(), params.getTo());

        ConnectableObservable<Message> messages = candidates
                .compose(new BackpressureAsyncTransformer<>())
                .map(tokenizer::tokenize)
                .publish();
        Observable<List<Message>> bufferedMessages = messages.buffer(10, TimeUnit.SECONDS, 3);

        allSubscriptions = new SubscriptionGroupLatch(2);
        Subscription subscription = messages.subscribe(message -> {
            logger.info("READ: \"{}\"", message.getText());
        });
        Subscription bufferedSubscription = bufferedMessages.subscribe(new BufferedMessageListObserver(allSubscriptions));
        allSubscriptions.setSubscriptions(subscription, bufferedSubscription);

        messages.connect();

        allSubscriptions.waitAllUnsubscribed();

        logger.info("Done.");
    }
}
