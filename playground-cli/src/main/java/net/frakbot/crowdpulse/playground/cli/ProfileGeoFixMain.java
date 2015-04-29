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

package net.frakbot.crowdpulse.playground.cli;

import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;
import net.frakbot.crowdpulse.common.util.rx.BackpressureAsyncTransformer;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.rx.BufferedProfileListObserver;
import net.frakbot.crowdpulse.fixgeoprofile.IProfileGeoFixer;
import net.frakbot.crowdpulse.fixgeoprofile.googlemaps.GoogleMapsProfileGeoFixer;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class ProfileGeoFixMain {
    private SubscriptionGroupLatch allSubscriptions;
    private IProfileGeoFixer profileGeoFixer;

    public static void main(String[] args) throws IOException {
        ProfileGeoFixMain main = new ProfileGeoFixMain();
        main.profileGeoFixer = new GoogleMapsProfileGeoFixer();
        main.run(args);
    }

    public void run(String[] args) throws IOException {
        GenericAnalysisParameters params = MainHelper.start(args);
        final Observable<Profile> candidates = MainHelper.getGeoConsolidationProfileCandidates(params.getFrom(), params.getTo());

        ConnectableObservable<Profile> profiles = candidates
                .compose(new BackpressureAsyncTransformer<>())
                .map(profileGeoFixer::geoFixProfile)
                .publish();
        Observable<List<Profile>> bufferedProfiles = profiles.buffer(10, TimeUnit.SECONDS, 3);

        allSubscriptions = new SubscriptionGroupLatch(2);
        Subscription subscription = profiles.subscribe(
                profile -> MainHelper.getLogger().info("READ: \"{}\"", profile.getUsername()),
                throwable -> allSubscriptions.countDown(),
                allSubscriptions::countDown);
        Subscription bufferedSubscription = bufferedProfiles.subscribe(new BufferedProfileListObserver
                (allSubscriptions));
        allSubscriptions.setSubscriptions(subscription, bufferedSubscription);

        profiles.connect();

        allSubscriptions.waitAllUnsubscribed();

        MainHelper.getLogger().info("Done.");
    }
}
