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

import facebook4j.*;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;
import facebook4j.json.DataObjectFactory;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.social.facebook.FacebookFactory;
import net.frakbot.crowdpulse.social.profile.ProfileParameters;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Francesco Pontillo
 */
public class FacebookProfilerRunner {
    private static final Logger logger = CrowdLogger.getLogger(FacebookProfilerRunner.class);

    public List<Profile> getProfiles(ProfileParameters parameters) {
        Profile profile = null;
        String username = parameters.getProfiles().get(0);
        try {
            long followings = 0;
            long followers = 0;
            String objectType = null;
            Object userOrPage = null;

            // get the raw message so we can determine if it is a user or a page
            Reading includeMetadata = new Reading();
            includeMetadata.metadata();
            HashMap<String, String> rawMap = new HashMap<>();
            rawMap.put("metadata", "1");
            RawAPIResponse res = FacebookFactory.getFacebookInstance().rawAPI()
                    .callGetAPI(username, rawMap);

            // we now have the full JSON, so we go into it and check the object type
            JSONObject json = res.asJSONObject();
            String jsonType;
            try {
                jsonType = json.getJSONObject("metadata").getString("type");
            } catch (JSONException e) {
                jsonType = null;
            }

            Page locationPage = null;

            assert jsonType != null;

            if (jsonType.equals("user")) {
                // if the profile is a user, fetch both friends and followers (subscribers)
                userOrPage = DataObjectFactory.createUser(res.asString());
                objectType = FacebookProfileConverter.DATA_OBJECT_TYPE_USER;
                try {
                    followings = FacebookFactory.getFacebookInstance().friends().getFriends(
                            username).getCount();
                } catch (FacebookException ignored) {}
                try {
                    followers = FacebookFactory.getFacebookInstance().getSubscribers(
                            username).getCount();
                } catch (FacebookException ignored) {}
                if (((User) userOrPage).getLocation() != null) {
                    locationPage = FacebookFactory.getFacebookInstance()
                            .getPage(((User) userOrPage).getLocation().getId());
                }
            } else if (jsonType.equals("page")) {
                // if the profile is a page, followings stays to 0
                userOrPage = DataObjectFactory.createPage(res.asString());
                objectType = FacebookProfileConverter.DATA_OBJECT_TYPE_PAGE;
                Page page = (Page) userOrPage;
                followers = page.getLikes();
                locationPage = page;
            }

            HashMap<String, Object> conversionMap = new HashMap<>();
            conversionMap.put(FacebookProfileConverter.DATA_OBJECT_TYPE, objectType);
            conversionMap.put(FacebookProfileConverter.DATA_FOLLOWINGS_COUNT, followings);
            conversionMap.put(FacebookProfileConverter.DATA_FOLLOWERS_COUNT, followers);
            conversionMap.put(FacebookProfileConverter.DATA_LOCATION_PAGE, locationPage);

            // convert the user or page
            profile = new FacebookProfileConverter(parameters).fromExtractor(userOrPage, conversionMap);
        } catch (FacebookException ignored) { }
        return Collections.singletonList(profile);
    }

}
