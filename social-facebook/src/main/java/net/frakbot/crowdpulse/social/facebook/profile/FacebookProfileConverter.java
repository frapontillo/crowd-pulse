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

import facebook4j.Page;
import facebook4j.User;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.social.profile.ProfileParameters;
import net.frakbot.crowdpulse.social.profile.ProfileConverter;
import net.frakbot.crowdpulse.social.util.StringUtil;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Francesco Pontillo
 */
public class FacebookProfileConverter extends ProfileConverter<Object> {

    public static final String DATA_OBJECT_TYPE = "DATA_OBJECT_TYPE";
    public static final String DATA_OBJECT_TYPE_USER = "DATA_OBJECT_TYPE_USER";
    public static final String DATA_OBJECT_TYPE_PAGE = "DATA_OBJECT_TYPE_PAGE";
    public static final String DATA_ACTIVATION_DATE = "DATA_ACTIVATION_DATE";
    public static final String DATA_FOLLOWINGS_COUNT = "DATA_FOLLOWINGS_COUNT";
    public static final String DATA_FOLLOWERS_COUNT = "DATA_FOLLOWERS_COUNT";
    public static final String DATA_LOCATION_PAGE = "DATA_LOCATION_PAGE";

    public FacebookProfileConverter(ProfileParameters parameters) {
        super(parameters);
    }

    @Override protected Profile fromSpecificExtractor(Object original, HashMap<String, Object> additionalData) {
        Profile profile = new Profile();

        if (additionalData == null) {
            return null;
        }
        String objectType = (String) additionalData.get(DATA_OBJECT_TYPE);
        if (objectType.equals(DATA_OBJECT_TYPE_USER)) {
            User user = (User) original;
            profile.setUsername(user.getId());
            // set the language as ISO-3 ("en", "it", etc.)
            profile.setLanguage(user.getLocale().getISO3Language());
        } else if (objectType.equals(DATA_OBJECT_TYPE_PAGE)) {
            profile.setUsername(((Page)original).getId());
            // TODO: find a way to set language for pages
        }

        Page locationPage = (Page) additionalData.get(DATA_LOCATION_PAGE);
        if (locationPage != null && locationPage.getLocation() != null) {
            // set coordinates
            if (locationPage.getLocation().getLongitude() != null) {
                profile.setLongitude(locationPage.getLocation().getLongitude());
            }
            if (locationPage.getLocation().getLatitude() != null) {
                profile.setLatitude(locationPage.getLocation().getLatitude());
            }
            // set the location name
            profile.setLocation(StringUtil.join(", ", true, locationPage.getLocation().getCity(),
                    locationPage.getLocation().getState(), locationPage.getLocation().getCountry()));
        }

        profile.setActivationDate((Date) additionalData.get(DATA_ACTIVATION_DATE));

        Long followings = (Long) additionalData.get(DATA_FOLLOWINGS_COUNT);
        profile.setFollowings(followings != null ? followings : 0);

        Long followers = (Long) additionalData.get(DATA_FOLLOWERS_COUNT);
        profile.setFollowers(followers != null ? followers : 0);

        return profile;
    }

}
