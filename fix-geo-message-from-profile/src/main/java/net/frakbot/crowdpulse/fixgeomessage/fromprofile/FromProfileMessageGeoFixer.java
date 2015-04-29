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

package net.frakbot.crowdpulse.fixgeomessage.fromprofile;

import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import net.frakbot.crowdpulse.fixgeomessage.IMessageGeoFixer;

/**
 * @author Francesco Pontillo
 */
public class FromProfileMessageGeoFixer extends IMessageGeoFixer {
    private final String GEOFIXER_IMPL = "fromprofile";
    private final ProfileRepository profileRepository;

    public FromProfileMessageGeoFixer() {
        profileRepository = new ProfileRepository();
    }

    @Override public Double[] getCoordinates(Message message) {
        Profile user = profileRepository.getByUsername(message.getFromUser());
        Double[] coordinates = null;
        if (user != null && user.getLatitude() != null && user.getLongitude() != null) {
            coordinates = new Double[] { user.getLatitude(), user.getLongitude() };
        }
        return coordinates;
    }

    @Override public String getName() {
        return GEOFIXER_IMPL;
    }
}
