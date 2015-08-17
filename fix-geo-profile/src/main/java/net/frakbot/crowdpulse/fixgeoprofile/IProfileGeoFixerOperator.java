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
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Profile;
import rx.Observable;
import rx.Subscriber;

/**
 * Rx {@link rx.Observable.Operator} that accepts and outputs {@link Profile}s after attempting a geo-location fix on
 * them.
 * <p>
 * Clients should implement {@link #getCoordinates(Profile)}.
 *
 * @author Francesco Pontillo
 */
public abstract class IProfileGeoFixerOperator implements Observable.Operator<Profile, Profile> {
    private IPlugin plugin;

    public IProfileGeoFixerOperator(IPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Subscriber<? super Profile> call(Subscriber<? super Profile> subscriber) {
        return new CrowdSubscriber<Profile>(subscriber) {
            @Override
            public void onNext(Profile profile) {
                plugin.reportElementAsStarted(profile.getId());
                profile = geoFixProfile(profile);
                plugin.reportElementAsEnded(profile.getId());
                subscriber.onNext(profile);
            }

            @Override public void onCompleted() {
                plugin.reportPluginAsCompleted();
                super.onCompleted();
            }

            @Override public void onError(Throwable e) {
                plugin.reportPluginAsErrored();
                super.onError(e);
            }
        };
    }

    /**
     * Fixes the geo-location of a {@link Profile} by calling the actual {@link #getCoordinates(Profile)}
     * implementation.
     *
     * @param profile The {@link Profile} to fix the geo-location for.
     * @return The same input {@link Profile} with an eventual geo-location set to it.
     */
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
     *
     * @param profile The {@link Profile} to retrieve coordinates for.
     * @return Array of {@link Double} containing, in order, latitude and longitude.
     */
    public abstract Double[] getCoordinates(Profile profile);

}
