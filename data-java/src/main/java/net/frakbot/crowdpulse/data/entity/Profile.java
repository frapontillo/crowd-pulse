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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Profiles are entities describing the user information available on the social network being analyzed.
 *
 * @author Francesco Pontillo
 */
public class Profile extends Entity implements Comparable<Profile> {
    private String source;
    private String username;
    private List<String> customTags;
    private Date activationDate;
    private long followers;
    private long followings;
    private String language;
    private String location;
    private Double latitude;
    private Double longitude;
    private List<String> connections;

    public Profile() {
        connections = new ArrayList<>();
    }

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

    public List<String> getCustomTags() {
        return customTags;
    }

    public void setCustomTags(List<String> customTags) {
        this.customTags = customTags;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<String> getConnections() {
        return connections;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    public void addConnections(String... connections) {
        this.connections.addAll(Arrays.asList(connections));
    }

    @Override public String toString() {
        return getId().toString() + ":"
                + getSource() + ":"
                + getUsername();
    }

    public String getIdentityRepr() {
        return getSource() + ":" +  getUsername();
    }

    @Override public int compareTo(Profile o) {
        if (this.getSource().equals(o.getSource())) {
            return this.getUsername().compareTo(o.getUsername());
        }
        return this.getSource().compareTo(o.getSource());
    }
}
