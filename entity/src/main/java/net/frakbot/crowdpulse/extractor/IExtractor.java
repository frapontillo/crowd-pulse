/*
 * Copyright 2014 Francesco Pontillo
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

package net.frakbot.crowdpulse.extractor;

/**
 * @author Francesco
 */
public interface IExtractor {
    /**
     * Returns the name of the extractor implementation.
     * @return {@link java.lang.String} the name of the extractor.
     */
    public String getName();

    /**
     * Returns the maximum number of parameters that this extractor supports per each query.
     * @return {@link long} the maximum number of parameters per query.
     */
    public long getMaximumQueryParameters();

    /**
     * Check if the extractor supports regular text queries.
     * @return {@link boolean} true if there is support for text queries.
     */
    public boolean getSupportQuery();

    /**
     * Check if the extractor supports searching with geolocation parameters
     * (usually latitude, longitude, radius).
     * @return {@link boolean} true if there is support for geolocation.
     */
    public boolean getSupportGeoLocation();

    /**
     * Check if the extractor supports searching for hash tags.
     * @return {@link boolean} true if there is support for hash tags.
     */
    public boolean getSupportHashTag();

    /**
     * Check if the extractor supports searching for messages sent from
     * a specific social network user (implementation-dependent).
     * @return {@link boolean} true if there is support for origin searching.
     */
    public boolean getSupportFrom();

    /**
     * Check if the extractor supports searching for messages sent to
     * a specific social network user (implementation-dependent).
     * @return {@link boolean} true if there is support for destination searching.
     */
    public boolean getSupportTo();

    /**
     * Check if the extractor supports searching for messages that reference
     * a specific social network user (implementation-dependent).
     * @return {@link boolean} true if there is support for reference searching.
     */
    public boolean getSupportReference();

    /**
     * Check if the extractor supports searching for messages sent since a specific date.
     * @return {@link boolean} true if there is support for starting date searching.
     */
    public boolean getSupportSince();

    /**
     * Check if the extractor supports searching for messages sent until a specific date.
     * @return {@link boolean} true if there is support for ending date searching.
     */
    public boolean getSupportUntil();

    /**
     * Check if the extractor supports searching for messages written in a specific
     * language.
     * @return {@link boolean} true if there is support for language searching.
     */
    public boolean getSupportLanguage();

    /**
     * Check if the extractor supports searching for messages written in a specific
     * language locale.
     * @return {@link boolean} true if there is support for language locale searching.
     */
    public boolean getSupportLocale();
}
