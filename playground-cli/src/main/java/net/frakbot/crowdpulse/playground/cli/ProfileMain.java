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

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.rx.BufferedProfileListObserver;
import net.frakbot.crowdpulse.social.profile.IProfiler;
import net.frakbot.crowdpulse.social.profile.ProfileParameters;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class ProfileMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ProfileMain main = new ProfileMain();
        main.run(args);
    }

    public void run(String[] args) throws IOException, ClassNotFoundException {
        ProfileParameters params = new ProfileParameters();
        new JCommander(params, args);
        IProfiler profiler = PluginProvider.getPlugin(params.getSource());

        ConnectableObservable<Profile> profiles = profiler.getProfile(params);
        Observable<List<Profile>> bufferedMessages = profiles.buffer(10, TimeUnit.SECONDS, 3, Schedulers.io());

        SubscriptionGroupLatch allSubscriptions = new SubscriptionGroupLatch(1);
        Subscription bufferedSubscription = bufferedMessages.subscribe(new BufferedProfileListObserver(allSubscriptions));
        allSubscriptions.setSubscriptions(bufferedSubscription);

        profiles.connect();
        allSubscriptions.waitAllUnsubscribed();
        MainHelper.getLogger().info("Done.");
    }
}
