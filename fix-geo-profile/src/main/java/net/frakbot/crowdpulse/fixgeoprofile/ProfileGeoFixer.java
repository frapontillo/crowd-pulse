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

package net.frakbot.crowdpulse.fixgeoprofile;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author Francesco Pontillo
 */
public class ProfileGeoFixer {
    private final String PROP_GEOCODING_APIKEY = "geocoding.apiKey";
    private final ProfileRepository profileRepository = new ProfileRepository();

    public ConnectableObservable<Profile> getGeoConsolidatedProfiles(final ProfileGeoFixParameters parameters) {

        Observable<Profile> profiles = Observable.create(new Observable.OnSubscribe<Profile>() {
            @Override public void call(Subscriber<? super Profile> subscriber) {
                // read all of the candidates
                final List<Profile> subsceptibleProfiles = profileRepository.getGeoConsolidationCandidates(
                        parameters.getFrom(), parameters.getTo());
                // prepare the GeoApi service
                final GeoApiContext context = new GeoApiContext().setApiKey(readApiKey());

                for (Profile profile : subsceptibleProfiles) {
                    GeocodingResult[] results = null;
                    // for each profile, attempt a forward geocoding (from address to lat-lng)
                    try {
                        results = GeocodingApi.newRequest(context).address(profile.getLocation()).await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // edit and notify the profile only if lat-lng coordinates were found
                    if (results != null && results.length > 0) {
                        profile.setLatitude(results[0].geometry.location.lat);
                        profile.setLongitude(results[0].geometry.location.lng);
                        subscriber.onNext(profile);
                    }
                }

                subscriber.onCompleted();
            }
        });

        profiles = profiles.subscribeOn(Schedulers.io());
        profiles = profiles.observeOn(Schedulers.io());

        ConnectableObservable connectableProfiles = profiles.publish();
        return connectableProfiles;
    }

    private String readApiKey() {
        InputStream configInput = getClass().getClassLoader().getResourceAsStream("geocoding.properties");
        Properties prop = new Properties();
        try {
            prop.load(configInput);
        } catch (IOException ignored) {}
        return prop.getProperty(PROP_GEOCODING_APIKEY);
    }
}
