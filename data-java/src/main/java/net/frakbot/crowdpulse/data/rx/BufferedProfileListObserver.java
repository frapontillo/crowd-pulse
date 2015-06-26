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
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import org.apache.logging.log4j.Logger;
import rx.Observer;

import java.util.List;

/**
 * Generic buffered {@link Observer} for {@link Tag} that saves and print published tags.
 *
 * @author Francesco Pontillo
 */
public class BufferedProfileListObserver implements Observer<List<Profile>> {
    private final ProfileRepository profileRepository;
    private final Logger logger = CrowdLogger.getLogger(BufferedProfileListObserver.class);
    private SubscriptionGroupLatch allSubscriptions;

    /**
     * Construct a new BufferedProfileListObserver that takes care of saving profiles into the database as soon as they
     * arrive, buffered in {@link List}s.
     *
     * @param subscriptionGroupLatch A {@link SubscriptionGroupLatch} whose counter will be decremented as soon as
     *                               all {@link Profile}s are processed or if an exception is thrown.
     */
    public BufferedProfileListObserver(SubscriptionGroupLatch subscriptionGroupLatch) {
        profileRepository = new ProfileRepository();
        allSubscriptions = subscriptionGroupLatch;
    }

    @Override public void onCompleted() {
        logger.info("Buffered Profile stream ended.");
        allSubscriptions.countDown();
    }

    @Override public void onError(Throwable e) {
        logger.error("Buffered Profile Stream errored.");
        e.printStackTrace();
        allSubscriptions.countDown();
    }

    @Override public void onNext(List<Profile> profiles) {
        for (Profile profile : profiles) {
            profileRepository.save(profile);
            logger.info("SAVED: \"{}\"", profile.getUsername());
        }
    }
}
