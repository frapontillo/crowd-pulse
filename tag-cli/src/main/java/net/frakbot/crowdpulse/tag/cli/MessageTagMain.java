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
import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import net.frakbot.crowdpulse.tag.ITagger;
import net.frakbot.crowdpulse.tag.TaggerModule;
import org.apache.logging.log4j.Logger;
import rx.Observer;
import rx.observables.ConnectableObservable;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author Francesco Pontillo
 */
public class MessageTagMain {
    private final CountDownLatch endSignal = new CountDownLatch(2);
    private final Logger logger = CrowdLogger.getLogger(MessageTagMain.class);

    @Inject Set<ITagger> taggers;

    public static void main(String[] args) throws IOException {
        ObjectGraph objectGraph = ObjectGraph.create(new TaggerModule());
        MessageTagMain main = objectGraph.get(MessageTagMain.class);
        main.run(args);
    }

    public void run(String[] args) throws IOException {
        ITagger tagger = findTagger(taggers, "tagme");
        ConnectableObservable<Tag> tags = tagger.getTags("At around the size of a domestic chicken", "en");

        tags.subscribe(new TagObserver());

        tags.connect();

        logger.debug("Message tagging started.");

        // read parameters
        GenericAnalysisParameters params = new GenericAnalysisParameters();
        new JCommander(params, args);
        logger.debug("Parameters read.");

        // TODO: implement the tag retrieval

        logger.debug("Done.");
    }

    private static ITagger findTagger(Set<ITagger> taggers, String name) {
        for (ITagger tagger : taggers) {
            if (tagger.getName().equals(name)) {
                return tagger;
            }
        }
        return null;
    }

    private class MessageObserver implements Observer<Message> {
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
            // TODO: print found tags
            logger.info(String.format(
                    "%s@%s", message.getText(), ""));
        }
    }

    private class BufferedMessageListObserver implements Observer<List<Message>> {
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

    private class TagObserver implements Observer<Tag> {

        @Override public void onCompleted() {
            logger.debug("Tag Stream ended.");
            endSignal.countDown();
        }

        @Override public void onError(Throwable e) {
            logger.error("Tag Stream errored.");
            e.printStackTrace();
            endSignal.countDown();
        }

        @Override public void onNext(Tag tag) {
            logger.info(tag.getText());
        }
    }
}
