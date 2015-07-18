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

package net.frakbot.crowdpulse.social.facebook;

import facebook4j.Facebook;
import facebook4j.FacebookException;

/**
 * @author Francesco Pontillo
 */
public class FacebookFactory {
    private static Facebook facebook;

    /**
     * Returns a singleton instance of the {@link facebook4j.Facebook} client.
     *
     * @return A set up and ready {@link facebook4j.Facebook} client.
     * @throws facebook4j.FacebookException if the client could not be built.
     */
    public static Facebook getFacebookInstance() throws FacebookException {
        if (facebook == null) {
            facebook = new facebook4j.FacebookFactory().getInstance();
            facebook.getOAuthAppAccessToken();
        }
        return facebook;
    }
}
