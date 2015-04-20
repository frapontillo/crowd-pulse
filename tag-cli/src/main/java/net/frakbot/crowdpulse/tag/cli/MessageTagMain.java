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

package net.frakbot.crowdpulse.tag.cli;

import com.beust.jcommander.JCommander;
import dagger.ObjectGraph;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.rx.CompositeSubscriptionLatch;
import net.frakbot.crowdpulse.common.util.rx.RxUtil;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import net.frakbot.crowdpulse.data.repository.TagRepository;
import net.frakbot.crowdpulse.tag.MessageTagParameters;
import net.frakbot.crowdpulse.tag.MessageTagger;
import net.frakbot.crowdpulse.tag.TaggerModule;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class MessageTagMain {
    private CompositeSubscriptionLatch allSubscriptions;
    private final Logger logger = CrowdLogger.getLogger(MessageTagMain.class);
    private MessageTagger tagger;

    public static void main(String[] args) throws IOException {
        ObjectGraph objectGraph = ObjectGraph.create(new TaggerModule());
        MessageTagMain main = new MessageTagMain();
        main.tagger = objectGraph.get(MessageTagger.class);
        main.run(args);
    }

    public void run(String[] args) throws IOException {
        logger.debug("Message tagging started.");

        // read parameters
        MessageTagParameters params = new MessageTagParameters();
        new JCommander(params, args);
        logger.debug("Parameters read.");

        ConnectableObservable<Message> messages = tagger.tagMessages(params);
        // save all messages
        Observable<List<Message>> bufferedMessages = messages.buffer(10, TimeUnit.SECONDS, 3, Schedulers.io());
        // get all unique tags
        Observable<Tag> uniqueTags = messages
                .flatMap(new Func1<Message, Observable<Tag>>() {
                    @Override public Observable<Tag> call(Message message) {
                        return Observable.from(message.getTags());
                    }
                })
                .distinct(new Func1<Tag, String>() {
                    @Override public String call(Tag tag) {
                        return tag.getText();
                    }
                });

        allSubscriptions = new CompositeSubscriptionLatch(2);
        Subscription bufferedSubscription = bufferedMessages.subscribe(new BufferedMessageListObserver());
        Subscription tagSubscription = uniqueTags.subscribe(new TagObserver());
        allSubscriptions.setSubscriptions(bufferedSubscription, tagSubscription);

        messages.connect();

        allSubscriptions.waitAllUnsubscribed();

        logger.debug("Done.");
    }

    private class BufferedMessageListObserver implements Observer<List<Message>> {
        private final MessageRepository messageRepository;

        public BufferedMessageListObserver() {
            messageRepository = new MessageRepository();
        }

        @Override public void onCompleted() {
            logger.debug("Buffered Message stream ended.");
            allSubscriptions.countDown();
        }

        @Override public void onError(Throwable e) {
            logger.error("Buffered Message Stream errored.");
            e.printStackTrace();
            allSubscriptions.countDown();
        }

        @Override public void onNext(List<Message> messages) {
            for (Message message : messages) {
                logger.info(String.format("%s", message.getText()));
                messageRepository.save(message);
            }
        }
    }

    private class TagObserver implements Observer<Tag> {
        TagRepository tagRepository = new TagRepository();

        @Override public void onCompleted() {
            logger.debug("Tag Stream ended.");
            allSubscriptions.countDown();
        }

        @Override public void onError(Throwable e) {
            logger.error("Tag Stream errored.");
            e.printStackTrace();
            allSubscriptions.countDown();
        }

        @Override public void onNext(Tag tag) {
            logger.info(tag.getText());
            tagRepository.insertOrUpdate(tag);
        }
    }
}
