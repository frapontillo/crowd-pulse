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

package net.frakbot.crowdpulse.social.cli.extraction;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import net.frakbot.crowdpulse.social.cli.ExtractorCollection;
import net.frakbot.crowdpulse.social.extraction.ExtractionParameters;
import net.frakbot.crowdpulse.social.extraction.Extractor;
import net.frakbot.crowdpulse.social.facebook.extraction.FacebookExtractor;
import net.frakbot.crowdpulse.social.twitter.extraction.TwitterExtractor;
import net.frakbot.crowdpulse.social.util.Logger;
import net.frakbot.crowdpulse.social.util.StringUtil;
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
public class ExtractionMain {

    static {
        ExtractorCollection.registerExtractor(new TwitterExtractor());
        ExtractorCollection.registerExtractor(new FacebookExtractor());
    }

    private static final CountDownLatch endSignal = new CountDownLatch(2);

    public static void main(String[] args) throws IOException {

        Logger.getLogger().debug("Extraction started.");

        ExtractionParameters params = new ExtractionParameters();
        new JCommander(params, args);
        Logger.getLogger().debug("Parameters read.");
        Extractor extractor = ExtractorCollection.getExtractorImplByParams(params);

        ConnectableObservable<Message> messages = extractor.getMessages(params);
        Observable<List<Message>> bufferedMessages = messages.buffer(1, TimeUnit.SECONDS, 3, Schedulers.io());

        Subscription subscription = messages.subscribe(new MessageObserver(params));
        Subscription bufferedSubscription = bufferedMessages.subscribe(new BufferedMessageListObserver(params));

        messages.connect();

        // the thread can be interrupted while "await"-ing, so we "await" again until the subscription is over
        while (!subscription.isUnsubscribed() || !bufferedSubscription.isUnsubscribed()) {
            try {
                endSignal.await();
            } catch (InterruptedException ignore) { }
        }

        Logger.getLogger().debug("Done.");
    }

    private static class MessageObserver implements Observer<Message> {
        private final ExtractionParameters parameters;
        private String tags;

        public MessageObserver(ExtractionParameters params) {
            parameters = params;
            tags = StringUtil.join(parameters.getTags(), ",");
            if (!StringUtil.isNullOrEmpty(tags)) {
                tags += " | ";
            }
        }

        @Override public void onCompleted() {
            Logger.getLogger().debug("Message Stream ended.");
            endSignal.countDown();
        }

        @Override public void onError(Throwable e) {
            Logger.getLogger().error("Message Stream errored.");
            e.printStackTrace();
            endSignal.countDown();
        }

        @Override public void onNext(Message message) {
            Logger.getLogger().info(tags + message.getText());
        }
    }

    private static class BufferedMessageListObserver implements Observer<List<Message>> {
        private final MessageRepository messageRepository;
        private final ExtractionParameters parameters;

        public BufferedMessageListObserver(ExtractionParameters params) {
            messageRepository = new MessageRepository();
            parameters = params;
        }

        @Override public void onCompleted() {
            Logger.getLogger().debug("Buffered Message stream ended.");
            endSignal.countDown();
        }

        @Override public void onError(Throwable e) {
            Logger.getLogger().error("Buffered Message Stream errored.");
            e.printStackTrace();
            endSignal.countDown();
        }

        @Override public void onNext(List<Message> messages) {
            for (Message message : messages) {
                message.setTags(parameters.getTags());
                messageRepository.save(message);
            }
        }
    }
}
