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

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.social.profile.IProfileGrapher;
import net.frakbot.crowdpulse.social.profile.ProfileParameters;
import net.frakbot.crowdpulse.social.twitter.TwitterFactory;
import org.apache.logging.log4j.Logger;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class TwitterProfileGrapher extends IProfileGrapher {
    public final static String PLUGIN_NAME = "twitter-profile-grapher";
    private final static int MAX_FRIENDS_PER_REQUEST = 200;
    private static final Logger logger = CrowdLogger.getLogger(TwitterProfileGrapher.class);

    @Override public List<Profile> getConnections(Profile profile, ProfileParameters parameters) {
        TwitterProfileConverter converter = new TwitterProfileConverter(parameters);
        List<Profile> profiles = new ArrayList<>();
        PagableResponseList<User> friends = null;
        Long cursor = null;
        try {
            Twitter twitter = TwitterFactory.getTwitterInstance();
            // loop until there's a next valid cursor
            do {
                try {
                    long actualCursor = cursor != null ? cursor : -1;
                    friends = twitter
                            .getFriendsList(profile.getUsername(), actualCursor, MAX_FRIENDS_PER_REQUEST, true, false);
                } catch (TwitterException timeout) {
                    if (TwitterFactory.waitForTwitterTimeout(timeout, logger)) {
                        continue;
                    }
                }
                converter.addFromExtractor(friends, profiles);
                // if friends were indeed found, get the next cursor, otherwise default to -1
                if (friends != null) {
                    cursor = friends.getNextCursor();
                } else {
                    cursor = -1L;
                }
            } while (cursor == null || cursor > 0);
        } catch (TwitterException | InterruptedException e) {
            e.printStackTrace();
        }
        return profiles;
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }
}
