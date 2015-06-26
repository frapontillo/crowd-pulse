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
 * Refresh token for the OAuth 2.0 implementation.
 *
 * @author Francesco Pontillo
 */
public class RefreshToken extends Entity {
    private String refreshToken;
    @Reference private App app;
    @Reference private User user;
    private Date expires;

    /**
     * Get the refresh token.
     *
     * @return The refresh token.
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Set a new refresh token.
     *
     * @param refreshToken The new refresh token.
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Get the {@link App} the refresh token is associated to.
     *
     * @return The {@link App} the refresh token is associated to.
     */
    public App getApp() {
        return app;
    }

    /**
     * Set the {@link App} the refresh token is associated to.
     *
     * @param app The {@link App} the refresh token is associated to.
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * Get the {@link User} the refresh token was issued for.
     *
     * @return The {@link User} the refresh token was issued for.
     */
    public User getUser() {
        return user;
    }

    /**
     * Set the {@link User} the refresh token was issued for.
     *
     * @param user The {@link User} the refresh token was issued for.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the {@link Date} of expiration for the refresh token. A new token will have to be generated.
     *
     * @return The {@link Date} of expiration of the refresh token.
     */
    public Date getExpires() {
        return expires;
    }

    /**
     * Get the {@link Date} of expiration for the refresh token.
     *
     * @param expires The {@link Date} of expiration of the refresh token.
     */
    public void setExpires(Date expires) {
        this.expires = expires;
    }
}
