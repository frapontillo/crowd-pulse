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

package net.frakbot.crowdpulse.data.entity;

import org.mongodb.morphia.annotations.Reference;

import java.util.Date;

/**
 * Model for the OAuth 2.0 access token.
 *
 * @author Francesco Pontillo
 */
public class AccessToken extends Entity {
    private String accessToken;
    @Reference private App app;
    @Reference private User user;
    private Date expires;

    /**
     * Get the access token.
     *
     * @return The access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Set the access token.
     *
     * @param accessToken The access token.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Get the {@link App} the token was issued for.
     *
     * @return The {@link App} the token was issued for.
     */
    public App getApp() {
        return app;
    }

    /**
     * Set the {@link App} the token was issued for.
     *
     * @param app The {@link App} the token was issued for.
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * Get the {@link User} the token was issued for.
     *
     * @return The {@link User} the token was issued for.
     */
    public User getUser() {
        return user;
    }

    /**
     * Set the {@link User} the token was issued for.
     *
     * @param user The {@link User} the token was issued for.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the {@link Date} of expiration for the access token. A new token will have to be generated.
     *
     * @return The {@link Date} of expiration of the access token.
     */
    public Date getExpires() {
        return expires;
    }

    /**
     * Get the {@link Date} of expiration for the access token.
     *
     * @param expires The {@link Date} of expiration of the access token.
     */
    public void setExpires(Date expires) {
        this.expires = expires;
    }
}
