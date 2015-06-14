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
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * @author Francesco Pontillo
 */
public class TwitterProfilerRunner {
    private static final org.apache.logging.log4j.Logger logger = CrowdLogger.getLogger(TwitterProfilerRunner.class);

    public Profile getSingleProfile(ProfileParameters parameters) {
        Profile profile = null;
        try {
            User user = TwitterFactory.getTwitterInstance().showUser(parameters.getProfile());
            profile = new TwitterProfileConverter(parameters).fromExtractor(user, null);
        } catch (TwitterException ignored) { }
        return profile;
    }
}
