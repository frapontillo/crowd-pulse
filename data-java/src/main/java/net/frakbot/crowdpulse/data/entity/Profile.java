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

import java.util.Date;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class Profile extends Entity {
    private String source;
    private String username;
    private List<String> tags;
    private Date activationDate;
    private long followers;
    private long followings;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long follwers) {
        this.followers = follwers;
    }

    public long getFollowings() {
        return followings;
    }

    public void setFollowings(long following) {
        this.followings = following;
    }
}
