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

    /**
     * Create a new Profile.
     */
    public Profile() {
        connections = new ArrayList<>();
    }

    /**
     * Get the source this Profile was extracted (or originated) from.
     *
     * @return A {@link String} describing the source of the Profile.
     */
    public String getSource() {
        return source;
    }

    /**
     * Set the source of the Profile as a {@link String}.
     *
     * @param source The source of the Profile.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Get the username associated to this Profile.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username associated to the Profile.
     *
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the {@link List} of custom tags associated with the Profile.
     *
     * @return A {@link List} of {@link String} tags.
     */
    public List<String> getCustomTags() {
        return customTags;
    }

    /**
     * Set the {@link List} of custom tags for the Profile.
     *
     * @param customTags A {@link List} of {@link String} tags.
     */
    public void setCustomTags(List<String> customTags) {
        this.customTags = customTags;
    }

    /**
     * Get the {@link Date} the user registered on the social network.
     *
     * @return The {@link Date} of the user registration.
     */
    public Date getActivationDate() {
        return activationDate;
    }

    /**
     * Set the {@link Date} the user registered on the social network.
     *
     * @param activationDate The {@link Date} of the user registration.
     */
    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    /**
     * Get the number of followers (users who follow the Profile) the user has on the social network at extraction
     * time.
     *
     * @return The number of followers.
     */
    public long getFollowers() {
        return followers;
    }

    /**
     * Set the number of followers (users who follow the Profile) the user has on the social network at extraction
     * time.
     *
     * @param follwers The number of followers.
     */
    public void setFollowers(long follwers) {
        this.followers = follwers;
    }

    /**
     * Get the number of users the Profile follows on the social network at extraction time.
     *
     * @return The number of people being followed by the Profile.
     */
    public long getFollowings() {
        return followings;
    }

    /**
     * Set the number of users the Profile follows on the social network at extraction time.
     *
     * @param following The number of people being followed by the Profile.
     */
    public void setFollowings(long following) {
        this.followings = following;
    }

    /**
     * Get the language associated to the user Profile.
     *
     * @return The language set in the Profile.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Change the language associated to the Profile.
     *
     * @param language The language of the Profile.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Get the location of the Profile as a {@link String}. This piece of information may not always be available.
     *
     * @return The {@link String} location of the user Profile.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of the Profile as a {@link String}.
     *
     * @param location The {@link String} location of the user Profile.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get the latitude of the user, as specified in the Profile (may be null or approximate).
     *
     * @return The latitude of the user.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Set the latitude of the user in the Profile.
     *
     * @param latitude The latitude of the user.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Get the longitude of the user, as specified in the Profile (may be null or approximate).
     *
     * @return The longitude of the user.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Set the longitude of the user in the Profile.
     *
     * @param longitude The latitude of the user.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the username of the Profile friends (followings).
     *
     * @return The {@link List} of people being followed by the Profile.
     */
    public List<String> getConnections() {
        return connections;
    }

    /**
     * Set a new {@link List} of usernames the Profile follows.
     *
     * @param connections The {@link List} of people being followed by the Profile.
     */
    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    /**
     * Add some usernames to the Profile's friends.
     *
     * @param connections The new connections of the Profile.
     */
    public void addConnections(String... connections) {
        this.connections.addAll(Arrays.asList(connections));
    }

    /**
     * Get a representation of the Profile by including the source and the username. This representation is called
     * identity as there is no more than one profile with the same representation.
     *
     * @return The identity representation of the Profile.
     */
    public String getIdentityRepr() {
        return getSource() + ":" + getUsername();
    }

    /**
     * Get a {@link String} representation of the Profile by including its id and identity (see {@link
     * #getIdentityRepr()}).
     *
     * @return The {@link String} representation of the Profile.
     */
    @Override public String toString() {
        return getId().toString() + ":" + getIdentityRepr();
    }

    /**
     * Compare two Profiles by giving priority to the source, then eventually to the username.
     * Profiles will then be ordered by (source, username).
     *
     * @param o The Profile to compare to the current one.
     * @return An negative integer if the Profile is logically previous to the input one, 0 if it is the same, positive
     * if it is logically next to the input one.
     */
    @Override public int compareTo(Profile o) {
        if (this.getSource().equals(o.getSource())) {
            return this.getUsername().compareTo(o.getUsername());
        }
        return this.getSource().compareTo(o.getSource());
    }
}
