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
import org.apache.logging.log4j.Logger;
import rx.Observer;

/**
 * @author Francesco Pontillo
 */
public class ProfilePrintObserver implements Observer<Profile> {
    private static final Logger logger = CrowdLogger.getLogger(ProfilePrintObserver.class);
    private final SubscriptionGroupLatch latch;

    public ProfilePrintObserver(SubscriptionGroupLatch latch) {
        this.latch = latch;
    }

    @Override public void onCompleted() {
        logger.info("Profile stream completed.");
        latch.countDown();
    }

    @Override public void onError(Throwable e) {
        logger.error("Profile stream errored.", e);
        e.printStackTrace();
        latch.countDown();
    }

    @Override public void onNext(Profile profile) {
        logger.debug("Completed work for profile \"{}\".", profile.toString());
    }
}
