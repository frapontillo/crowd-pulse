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

package net.frakbot.crowdpulse.social.facebook.profile;

import facebook4j.FacebookException;
import facebook4j.Friend;
import facebook4j.Reading;
import facebook4j.ResponseList;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.social.facebook.FacebookFactory;
import net.frakbot.crowdpulse.social.profile.IProfileGrapher;
import net.frakbot.crowdpulse.social.profile.ProfileParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Profile graph builder for Facebook API.
 *
 * Note: as of now, building the connection graph does not work for two reasons:
 * <ol>
 *     <li>a user's friends can be fetched only if the user has granted permission to the app</li>
 *     <li>a page's followers cannot be fetched at all</li>
 * </ol>
 * This graph builder has therefore been implemented as a sample.
 *
 * @author Francesco Pontillo
 */
public class FacebookProfileGrapher extends IProfileGrapher {
    public final static String PLUGIN_NAME = "facebook-profile-grapher";

    @Override public List<Profile> getConnections(Profile profile, ProfileParameters parameters) {
        FacebookProfileConverter converter = new FacebookProfileConverter(parameters);
        List<Profile> profiles = new ArrayList<>();
        ResponseList<Friend> friends;
        Reading reading = null;
        String cursor;
        try {
            do {
                friends = FacebookFactory.getFacebookInstance().getFriends(profile.getUsername(), reading);
                converter.addFromExtractor(friends, profiles);
                if ((cursor = friends.getPaging().getCursors().getAfter()) != null) {
                    reading = new Reading().after(cursor);
                }
            } while (cursor != null);
        } catch (FacebookException ignored) {}
        return profiles;
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }
}
