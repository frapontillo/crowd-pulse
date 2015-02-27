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

package net.frakbot.crowdpulse.fixgeoprofile.cli;

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import org.apache.logging.log4j.Logger;
import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import net.frakbot.crowdpulse.fixgeoprofile.ProfileGeoFixer;
import net.frakbot.crowdpulse.fixgeoprofile.ProfileGeoFixParameters;
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
public class ProfileGeoFixMain {
    private static final CountDownLatch endSignal = new CountDownLatch(2);
    private static final Logger logger = CrowdLogger.getLogger(ProfileGeoFixMain.class);

    public static void main(String[] args) throws IOException {
        logger.debug("Geo profile consolidation started.");

        // read parameters
        ProfileGeoFixParameters params = new ProfileGeoFixParameters();
        new JCommander(params, args);
        logger.debug("Parameters read.");

        // build the observables
        ProfileGeoFixer profileGeoFixer = new ProfileGeoFixer();
        ConnectableObservable<Profile> profiles = profileGeoFixer.getGeoConsolidatedProfiles(params);
        Observable<List<Profile>> bufferedProfiles = profiles.buffer(10, TimeUnit.SECONDS, 3, Schedulers.io());

        // subscribe to the profiles
        Subscription subscription = profiles.subscribe(new ProfileObserver());
        Subscription bufferedSubscription = bufferedProfiles.subscribe(new BufferedProfileListObserver());

        // start the actual observable creation
        profiles.connect();

        // the thread can be interrupted while "await"-ing, so we "await" again until the subscription is over
        while (!subscription.isUnsubscribed() || !bufferedSubscription.isUnsubscribed()) {
            try {
                endSignal.await();
            } catch (InterruptedException ignore) { }
        }

        logger.debug("Done.");
    }

    private static class ProfileObserver implements Observer<Profile> {
        @Override public void onCompleted() {
            logger.debug("Profile Stream ended.");
            endSignal.countDown();
        }

        @Override public void onError(Throwable e) {
            logger.error("Profile Stream errored.");
            e.printStackTrace();
            endSignal.countDown();
        }

        @Override public void onNext(Profile profile) {
            logger.info(String.format(
                    "%s@%f;%f", profile.getUsername(), profile.getLatitude(), profile.getLongitude()));
        }
    }

    private static class BufferedProfileListObserver implements Observer<List<Profile>> {
        private final ProfileRepository profileRepository;

        public BufferedProfileListObserver() {
            profileRepository = new ProfileRepository();
        }

        @Override public void onCompleted() {
            logger.debug("Buffered Profile stream ended.");
            endSignal.countDown();
        }

        @Override public void onError(Throwable e) {
            logger.error("Buffered Profile Stream errored.");
            e.printStackTrace();
            endSignal.countDown();
        }

        @Override public void onNext(List<Profile> profiles) {
            for (Profile profile : profiles) {
                profileRepository.save(profile);
            }
        }
    }
}
