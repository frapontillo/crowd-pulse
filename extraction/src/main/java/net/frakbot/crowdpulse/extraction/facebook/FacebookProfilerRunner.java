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

package net.frakbot.crowdpulse.extraction.facebook;

import facebook4j.*;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;
import facebook4j.json.DataObjectFactory;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.extraction.cli.ProfileParameters;
import net.frakbot.crowdpulse.extraction.util.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.util.HashMap;

/**
 * @author Francesco Pontillo
 */
public class FacebookProfilerRunner {

    public ConnectableObservable<Profile> getProfile(final ProfileParameters parameters) {

        // initialize the twitter instance
        try {
            FacebookFactory.getFacebookInstance();
        } catch (FacebookException e) {
            e.printStackTrace();
        }

        Observable<Profile> profiles = Observable.create(new Observable.OnSubscribe<Profile>() {
            @Override public void call(Subscriber<? super Profile> subscriber) {
                Logger.getLogger().info("PROFILER: started.");
                try {
                    // fetch and convert the user/page
                    long followings = 0;
                    long followers = 0;
                    String objectType = null;
                    Object userOrPage = null;

                    // get the raw message so we can determine if it is a user or a page
                    Reading includeMetadata = new Reading();
                    includeMetadata.metadata();
                    HashMap<String, String> rawMap = new HashMap<String, String>();
                    rawMap.put("metadata", "1");
                    RawAPIResponse res = FacebookFactory.getFacebookInstance().rawAPI().callGetAPI(parameters.getProfile(), rawMap);

                    // we now have the full JSON, so we go into it and check the object type
                    JSONObject json = res.asJSONObject();
                    String jsonType;
                    try {
                        jsonType = json.getJSONObject("metadata").getString("type");
                    } catch (JSONException e) {
                        jsonType = null;
                    }

                    if (jsonType.equals("user")) {
                        // if the profile is a user, fetch both friends and followers (subscribers)
                        userOrPage = DataObjectFactory.createUser(res.asString());
                        objectType = FacebookProfileConverter.DATA_OBJECT_TYPE_USER;
                        followings = FacebookFactory.getFacebookInstance().friends()
                                .getFriends(parameters.getProfile()).getCount();
                        followers = FacebookFactory.getFacebookInstance()
                                .getSubscribers(parameters.getProfile()).getCount();
                    } else if (jsonType.equals("page")) {
                        // if the profile is a page, followings stays to 0
                        userOrPage = DataObjectFactory.createPage(res.asString());
                        objectType = FacebookProfileConverter.DATA_OBJECT_TYPE_PAGE;
                        followers = ((Page) userOrPage).getLikes();
                    }

                    HashMap<String, Object> conversionMap = new HashMap<String, Object>();
                    conversionMap.put(FacebookProfileConverter.DATA_OBJECT_TYPE, objectType);
                    conversionMap.put(FacebookProfileConverter.DATA_FOLLOWINGS_COUNT, followings);
                    conversionMap.put(FacebookProfileConverter.DATA_FOLLOWERS_COUNT, followers);

                    // convert the user or page
                    Profile profile = new FacebookProfileConverter(parameters).fromExtractor(userOrPage, conversionMap);
                    // notify the user or page
                    subscriber.onNext(profile);
                } catch (FacebookException e) {
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
