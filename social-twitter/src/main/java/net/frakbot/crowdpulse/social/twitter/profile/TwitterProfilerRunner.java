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
import net.frakbot.crowdpulse.social.profile.ProfileParameters;
import net.frakbot.crowdpulse.social.twitter.TwitterFactory;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class TwitterProfilerRunner {
    private static final org.apache.logging.log4j.Logger logger = CrowdLogger.getLogger(TwitterProfilerRunner.class);

    public List<Profile> getProfiles(ProfileParameters parameters) {
        List<Profile> profiles = new ArrayList<>();
        boolean mustRepeat = false;
        do {
            try {
                String[] usernames = new String[parameters.getProfiles().size()];
                usernames = parameters.getProfiles().toArray(usernames);
                ResponseList<User> users = TwitterFactory.getTwitterInstance().lookupUsers(usernames);
                users.stream().forEach(u ->
                        profiles.add(new TwitterProfileConverter(parameters).fromExtractor(u, null)));
                mustRepeat = false;
            } catch (TwitterException twitterException) {
                try {
                    mustRepeat = TwitterFactory.waitForTwitterTimeout(twitterException, logger);
                } catch (InterruptedException ignored) {}
                // if the error isn't handled, just ignore it and return an empty list
                // e.g. a query for non-existing users may return 404, we don't care LOL
            }
        } while (mustRepeat);
        return profiles;
    }
}
