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
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.repository.TagRepository;
import org.apache.logging.log4j.Logger;
import rx.Observer;

import java.util.List;

/**
 * Generic buffered {@link Observer} for {@link Tag} that saves and print published tags.
 *
 * @author Francesco Pontillo
 */
public class BufferedTagListObserver implements Observer<List<Tag>> {
    private final TagRepository tagRepository;
    private final Logger logger = CrowdLogger.getLogger(BufferedTagListObserver.class);
    private SubscriptionGroupLatch allSubscriptions;

    /**
     * Construct a new {@link BufferedTagListObserver} that takes care of saving tags into the database
     * as soon as they arrive, buffered in {@link List}s.
     *
     * @param subscriptionGroupLatch A {@link SubscriptionGroupLatch} whose counter will be decremented as soon as
     *                               all {@link Tag}s are processed or if an exception is thrown.
     */
    public BufferedTagListObserver(SubscriptionGroupLatch subscriptionGroupLatch) {
        tagRepository = new TagRepository();
        allSubscriptions = subscriptionGroupLatch;
    }

    @Override public void onCompleted() {
        logger.info("Buffered Tag stream ended.");
        allSubscriptions.countDown();
    }

    @Override public void onError(Throwable e) {
        logger.error("Buffered Tag Stream errored.");
        e.printStackTrace();
        allSubscriptions.countDown();
    }

    @Override public void onNext(List<Tag> tags) {
        for (Tag tag : tags) {
            tagRepository.insertOrUpdate(tag);
            logger.info("SAVED: \"{}\"", tag.getText());
        }
    }
}
