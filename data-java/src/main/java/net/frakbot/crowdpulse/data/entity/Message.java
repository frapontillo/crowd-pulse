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

import net.frakbot.crowdpulse.common.util.StringUtil;

import java.util.*;

/**
 * Messages are one of the basic entities of Crowd Pulse: they can be extracted, manipulated, analyzed,
 * transformed and so on.
 *
 * @author Francesco Pontillo
 */
public class Message extends Entity {
    private String oId;
    private String text;
    private String source;
    private String fromUser;
    private List<String> toUsers;
    private List<String> refUsers;
    private Date date;
    private String parent;
    private List<String> customTags;
    private String language;
    private Double latitude;
    private Double longitude;
    private Integer favs;
    private Integer shares;
    private Set<Tag> tags;
    private List<Token> tokens;
    private double sentiment;

    /**
     * Get the original ID of the Message, according to the original source (Twitter, Facebook, etc.)
     *
     * @return The source ID of the Message.
     */
    public String getoId() {
        return oId;
    }

    /**
     * Set the ID the Message has in the original source.
     *
     * @param oId The source ID of the Message.
     */
    public void setoId(String oId) {
        this.oId = oId;
    }

    /**
     * Get the text of the Message.
     *
     * @return The Message body.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of the Message.
     *
     * @param text The body to set into the Message.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get the source this Message was extracted (or originated) from.
     *
     * @return A {@link String} describing the source of the Message.
     */
    public String getSource() {
        return source;
    }

    /**
     * Set the source of the Message as a {@link String}.
     *
     * @param source The source of the Message.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Get the Message author's user name or original id (depending from the implementation).
     *
     * @return A {@link String} representation of the author user.
     */
    public String getFromUser() {
        return fromUser;
    }

    /**
     * Set a {@link String} representation of the author user.
     *
     * @param fromUser The author user name or original id.
     */
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    /**
     * Retrieve the {@link List} of users the Message was addressed to.
     *
     * @return The users the Message is addressed to (can be empty).
     */
    public List<String> getToUsers() {
        return toUsers;
    }

    /**
     * Set the user this Message is addressed to.
     *
     * @param toUsers A {@link List} of user representations.
     */
    public void setToUsers(List<String> toUsers) {
        this.toUsers = toUsers;
    }

    /**
     * Get the {@link Date} the Message was created.
     *
     * @return The {@link Date} of creation.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the {@link Date} the Message was created.
     *
     * @param date The {@link Date} of creation.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get the original id of the parent Message in the nested conversation.
     *
     * @return The original id of the parent Message (can be null).
     */
    public String getParent() {
        return parent;
    }

    /**
     * Set the original id of the parent Message, that is the Message for which this one is a response
     * to.
     *
     * @param parent The original id of the parent Message.
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

    /**
     * Retrieve a {@link List} of user ids that the Message includes (by tagging, quoting, etc.).
     *
     * @return A {@link List} of user ids.
     */
    public List<String> getRefUsers() {
        return refUsers;
    }

    /**
     * Set a {@link List} of user ids that the Message includes (by tagging, quoting, etc.).
     *
     * @param refUsers A {@link List} of user ids.
     */
    public void setRefUsers(List<String> refUsers) {
        this.refUsers = refUsers;
    }

    /**
     * Get the {@link List} of custom tags associated with the Message.
     *
     * @return A {@link List} of {@link String} tags.
     */
    public List<String> getCustomTags() {
        return customTags;
    }

    /**
     * Set the {@link List} of custom tags for the Message.
     *
     * @param customTags A {@link List} of {@link String} tags.
     */
    public void setCustomTags(List<String> customTags) {
        this.customTags = customTags;
    }

    /**
     * Get the language of the Message as a IANA standard code (without country).
     * TODO: adopt strict RFC 3066.
     *
     * @return The language of the Message as a IANA standard code (no country).
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language of the Message as a IANA standard code (without country).
     * TODO: adopt strict RFC 3066.
     *
     * @param language The language of the Message as a IANA standard code (no country).
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Get the longitude associated to the Message.
     *
     * @return The longitude.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Set a longitude value into the Message.
     *
     * @param longitude The longitude.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the latitude associated to the Message.
     *
     * @return The latitude.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Set a latitude value into the Message.
     *
     * @param latitude The longitude.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Get the number of times the Message was favorited.
     *
     * @return The number of favs/likes.
     */
    public Integer getFavs() {
        return favs;
    }

    /**
     * Set the number of times the Message was favorited.
     *
     * @param favs The number of favs/likes.
     */
    public void setFavs(Integer favs) {
        this.favs = favs;
    }

    /**
     * Get the number of times the Message was shared on the social network.
     *
     * @return The number of shares/retweets.
     */
    public Integer getShares() {
        return shares;
    }

    /**
     * Set the number of times the Message was shared on the social network.
     *
     * @param shares The number of shares/retweets.
     */
    public void setShares(Integer shares) {
        this.shares = shares;
    }

    /**
     * Get the {@link Tag}s associated with the Message.
     *
     * @return A {@link Set} of {@link Tag}s.
     */
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Set the {@link Set} of {@link Tag}s associated with the Message.
     *
     * @param tags A {@link Set} of {@link Tag}s.
     */
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Add a {@link Set} of {@link Tag}s to the Message, without deleting the ones already set.
     *
     * @param tags The new {@link Set} of {@link Tag} with which the existing {@link Set} will be incremented.
     */
    public void addTags(Set<Tag> tags) {
        if (this.tags == null) {
            this.tags = tags;
        } else {
            this.tags.addAll(tags);
        }
    }

    /**
     * Add a {@link List} of {@link Tag}s to the already existing {@link Tag} {@link Set} of the Message,
     * without deleting the ones already set.
     *
     * @param tags The {@link List} of {@link Tag}s.
     */
    public void addTags(List<Tag> tags) {
        addTags(new HashSet<>(tags));
    }

    /**
     * Get the {@link List} of {@link Token}s associated to the Message text, if any.
     *
     * @return The {@link List} of {@link Token}s extracted from the text.
     */
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * Set a {@link List} of {@link Token}s associated to the Message.
     *
     * @param tokens The {@link List} of {@link Token}s extracted from the text.
     */
    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Get the sentiment value attached to the Message.
     *
     * @return The value of the sentiment associated to the Message text.
     */
    public double getSentiment() {
        return sentiment;
    }

    /**
     * Set the sentiment value of the Message.
     *
     * @param sentiment The value of the sentiment associated to the Message text.
     */
    public void setSentiment(double sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * Return a {@link String} representation of the Message with the following elements separated by a colon:
     * <ul>
     * <li>id of the Message</li>
     * <li>source of the Message</li>
     * <li>text of the Message, ellipsized if too long</li>
     * </ul>
     *
     * @return A brief description of the Message.
     */
    @Override public String toString() {
        return getId().toString() + ":"
                + getSource() + ":"
                + StringUtil.ellipsize(getText(), 20);
    }
}
