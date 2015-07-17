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

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Profile;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public abstract class IProfileGeoFixerOperator implements Observable.Operator<Profile, Profile> {

    @Override
    public Subscriber<? super Profile> call(Subscriber<? super Profile> subscriber) {
        return new CrowdSubscriber<Profile>(subscriber) {
            @Override
            public void onNext(Profile profile) {
                profile = geoFixProfile(profile);
                subscriber.onNext(profile);
            }
        };
    }

    protected Profile geoFixProfile(Profile profile) {
        Double[] coordinates = getCoordinates(profile);
        if (coordinates != null && coordinates.length == 2) {
            profile.setLatitude(coordinates[0]);
            profile.setLongitude(coordinates[1]);
        }
        return profile;
    }

    /**
     * Actual retrieval of {@link Profile} coordinates happens here.
     * @param profile The {@link Profile} to retrieve coordinates for.
     * @return Array of {@link Double} containing, in order, latitude and longitude.
     */
    public abstract Double[] getCoordinates(Profile profile);

}
