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

package net.frakbot.crowdpulse.data.rx;

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import org.apache.logging.log4j.Logger;
import rx.Observer;

import java.util.List;

/**
 * Generic buffered {@link Observer} for {@link Message} that saves and print published messages.
 *
 * @author Francesco Pontillo
 */
public class BufferedMessageListObserver implements Observer<List<Message>> {
    private final MessageRepository messageRepository;
    private final Logger logger = CrowdLogger.getLogger(BufferedMessageListObserver.class);
    private SubscriptionGroupLatch allSubscriptions;

    /**
     * Construct a new {@link BufferedMessageListObserver} that takes care of saving messages into the database
     * as soon as they arrive, buffered in {@link List}s.
     *
     * @param subscriptionGroupLatch A {@link SubscriptionGroupLatch} whose counter will be decremented as soon as
     *                               all {@link Message}s are processed or if an exception is thrown.
     */
    public BufferedMessageListObserver(SubscriptionGroupLatch subscriptionGroupLatch) {
        messageRepository = new MessageRepository();
        allSubscriptions = subscriptionGroupLatch;
    }

    @Override public void onCompleted() {
        logger.info("Buffered Message stream ended.");
        allSubscriptions.countDown();
    }

    @Override public void onError(Throwable e) {
        logger.error("Buffered Message Stream errored.");
        e.printStackTrace();
        allSubscriptions.countDown();
    }

    @Override public void onNext(List<Message> messages) {
        for (Message message : messages) {
            messageRepository.save(message);
            logger.info("SAVED: \"{}\"", message.getText());
        }
    }
}
