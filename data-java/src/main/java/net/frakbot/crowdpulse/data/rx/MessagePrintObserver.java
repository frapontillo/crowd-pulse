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
import org.apache.logging.log4j.Logger;
import rx.Observer;

/**
 * Generic {@link Observer} for {@link Message} that prints streamed {@link Message}s.
 *
 * @author Francesco Pontillo
 */
public class MessagePrintObserver implements Observer<Message> {
    private static final Logger logger = CrowdLogger.getLogger(MessagePrintObserver.class);
    private final SubscriptionGroupLatch latch;

    /**
     * Construct a new MessagePrintObserver.
     *
     * @param latch A {@link SubscriptionGroupLatch} whose counter will be decremented as soon as
     *              all {@link Message}s are processed or if an exception is thrown.
     */
    public MessagePrintObserver(SubscriptionGroupLatch latch) {
        this.latch = latch;
    }

    @Override public void onCompleted() {
        logger.info("Message stream completed.");
        latch.countDown();
    }

    @Override public void onError(Throwable e) {
        logger.error("Message stream errored.", e);
        e.printStackTrace();
        latch.countDown();
    }

    @Override public void onNext(Message message) {
        logger.info("Completed work for message \"{}\".", message.toString());
    }
}
