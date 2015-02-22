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

package net.frakbot.crowdpulse.social.cli.profile;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import net.frakbot.crowdpulse.social.facebook.profile.FacebookProfiler;
import net.frakbot.crowdpulse.social.profile.ProfileParameters;
import net.frakbot.crowdpulse.social.profile.Profiler;
import net.frakbot.crowdpulse.social.cli.ProfilerCollection;
import net.frakbot.crowdpulse.social.twitter.profile.TwitterProfiler;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.StringUtil;
import org.apache.logging.log4j.Logger;
import rx.Observer;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Francesco Pontillo
 */
public class ProfileMain {

    static {
        ProfilerCollection.registerProfiler(new TwitterProfiler());
        ProfilerCollection.registerProfiler(new FacebookProfiler());
    }

    private static final CountDownLatch endSignal = new CountDownLatch(1);
    private static final Logger logger = CrowdLogger.getLogger(ProfileMain.class);

    public static void main(String[] args) throws IOException {

        logger.debug("Profiling started.");

        ProfileParameters params = new ProfileParameters();
        new JCommander(params, args);
        logger.debug("Parameters read.");
        Profiler profiler = ProfilerCollection.getProfilerImplByParams(params);

        ConnectableObservable<Profile> profiles = profiler.getProfile(params);
        Subscription subscription = profiles.subscribe(new ProfileObserver(params));

        profiles.connect();

        // the thread can be interrupted while "await"-ing, so we "await" again until the subscription is over
        while (!subscription.isUnsubscribed()) {
            try {
                endSignal.await();
            } catch (InterruptedException ignore) { }
        }

        logger.debug("Done.");
    }

    private static class ProfileObserver implements Observer<Profile> {
        private final ProfileParameters parameters;
        private final ProfileRepository profileRepository;
        private String tags;

        public ProfileObserver(ProfileParameters params) {
            profileRepository = new ProfileRepository();
            parameters = params;
            tags = StringUtil.join(parameters.getTags(), ",");
            if (!StringUtil.isNullOrEmpty(tags)) {
                tags += " | ";
            }
        }

        @Override public void onCompleted() {
            logger.debug("Profiling ended.");
            endSignal.countDown();
        }

        @Override public void onError(Throwable e) {
            logger.error("Profiling errored.");
            e.printStackTrace();
            endSignal.countDown();
        }

        @Override public void onNext(Profile profile) {
            logger.info(tags + profile.getUsername());
            profileRepository.save(profile);
        }
    }
}
