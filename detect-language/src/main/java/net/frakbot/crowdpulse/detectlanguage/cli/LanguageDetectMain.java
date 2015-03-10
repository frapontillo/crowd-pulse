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

package net.frakbot.crowdpulse.detectlanguage.cli;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import net.frakbot.crowdpulse.detectlanguage.LanguageDetectParameters;
import net.frakbot.crowdpulse.detectlanguage.LanguageDetector;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class LanguageDetectMain {
    private static final CountDownLatch endSignal = new CountDownLatch(2);
    private static final Logger logger = CrowdLogger.getLogger(LanguageDetectMain.class);

    public static void main(String[] args) throws IOException {
        logger.debug("Message language detection started.");

        // read parameters
        LanguageDetectParameters params = new LanguageDetectParameters();
        new JCommander(params, args);
        logger.debug("Parameters read.");

        // build the observables
        LanguageDetector languageDetector = new LanguageDetector();
        ConnectableObservable<Message> messages = languageDetector.getMessagesWithLanguages(params);
        Observable<List<Message>> bufferedMessages = messages.buffer(10, TimeUnit.SECONDS, 3, Schedulers.io());

        // subscribe to the messages
        Subscription subscription = messages.subscribe(new MessageObserver());
        Subscription bufferedSubscription = bufferedMessages.subscribe(new BufferedMessageListObserver());

        // start the actual observable creation
        messages.connect();

        // the thread can be interrupted while "await"-ing, so we "await" again until the subscription is over
        while (!subscription.isUnsubscribed() || !bufferedSubscription.isUnsubscribed()) {
            try {
                endSignal.await();
            } catch (InterruptedException ignore) { }
        }

        logger.debug("Done.");
    }

    private static class MessageObserver implements Observer<Message> {
        @Override public void onCompleted() {
            logger.debug("Message Stream ended.");
            endSignal.countDown();
        }

        @Override public void onError(Throwable e) {
            logger.error("Message Stream errored.");
            e.printStackTrace();
            endSignal.countDown();
        }

        @Override public void onNext(Message message) {
            logger.info(String.format(
                    "%s@%s", message.getLanguage(), message.getText()));
        }
    }

    private static class BufferedMessageListObserver implements Observer<List<Message>> {
        private final MessageRepository messageRepository;

        public BufferedMessageListObserver() {
            messageRepository = new MessageRepository();
        }

        @Override public void onCompleted() {
            logger.debug("Buffered Message stream ended.");
            endSignal.countDown();
        }

        @Override public void onError(Throwable e) {
            logger.error("Buffered Message Stream errored.");
            e.printStackTrace();
            endSignal.countDown();
        }

        @Override public void onNext(List<Message> messages) {
            for (Message message : messages) {
                messageRepository.save(message);
            }
        }
    }
}
