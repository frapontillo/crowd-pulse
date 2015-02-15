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

package net.frakbot.crowdpulse.extraction.twitter;

import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.extraction.ProfileConverter;
import net.frakbot.crowdpulse.extraction.cli.ProfileParameters;
import twitter4j.User;

import java.util.HashMap;

/**
 * @author Francesco Pontillo
 */
public class TwitterProfileConverter extends ProfileConverter<User> {

    public TwitterProfileConverter(ProfileParameters parameters) {
        super(parameters);
    }

    @Override protected Profile fromSpecificExtractor(User original, HashMap<String, Object> additionalData) {
        Profile profile = new Profile();
        profile.setUsername(original.getScreenName());
        profile.setActivationDate(original.getCreatedAt());
        profile.setFollowings(original.getFriendsCount());
        profile.setFollowers(original.getFollowersCount());
        return profile;
    }

}
