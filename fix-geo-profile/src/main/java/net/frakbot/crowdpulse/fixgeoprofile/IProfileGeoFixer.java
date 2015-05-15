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

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Profile;
import rx.Observable;

/**
 * @author Francesco Pontillo
 */
public abstract class IProfileGeoFixer implements IPlugin {
    public abstract Double[] getCoordinates(Profile profile);

    public Profile geoFixProfile(Profile profile) {
        Double[] coordinates = getCoordinates(profile);
        if (coordinates != null && coordinates.length == 2) {
            profile.setLatitude(coordinates[0]);
            profile.setLongitude(coordinates[1]);
        }
        return profile;
    }

    /**
     * Process a stream of {@link Profile} and enrich each {@link Profile} with some kind
     * of geo-location information.
     *
     * @param profiles The stream of {@link Profile}s to process, as {@link Observable}.
     * @return A new {@link Observable} emitting the same items once the processing has happened.
     */
    public Observable<Profile> process(Observable<Profile> profiles) {
        return profiles.lift(new ProfileGeoFixerOperator(this));
    }
}
