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

package net.frakbot.crowdpulse.social.twitter.profile;

import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.social.profile.ProfileParameters;
import net.frakbot.crowdpulse.social.twitter.TwitterFactory;
import net.frakbot.crowdpulse.social.util.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * @author Francesco Pontillo
 */
public class TwitterProfilerRunner {

    public ConnectableObservable<Profile> getProfile(final ProfileParameters parameters) {

        // initialize the twitter instance
        try {
            TwitterFactory.getTwitterInstance();
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        Observable<Profile> profiles = Observable.create(new Observable.OnSubscribe<Profile>() {
            @Override public void call(Subscriber<? super Profile> subscriber) {
                Logger.getLogger().info("PROFILER: started.");
                try {
                    // fetch and convert the user
                    User user = TwitterFactory.getTwitterInstance().showUser(parameters.getProfile());
                    Profile profile = new TwitterProfileConverter(parameters).fromExtractor(user, null);
                    // notify the user
                    subscriber.onNext(profile);
                } catch (TwitterException e) {
                    subscriber.onError(e);
                }
                // immediately complete, there's nothing else to do
                subscriber.onCompleted();
                Logger.getLogger().info("PROFILER: ended.");
            }
        });

        profiles = profiles.subscribeOn(Schedulers.io());
        ConnectableObservable<Profile> connProfiles = profiles.publish();

        return connProfiles;
    }

}
