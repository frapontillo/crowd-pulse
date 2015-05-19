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

package net.frakbot.crowdpulse.social.extraction;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.social.exception.SocialException;
import net.frakbot.crowdpulse.social.exception.InvalidParametersSocialException;
import net.frakbot.crowdpulse.social.exception.MissingParametersSocialException;
import net.frakbot.crowdpulse.common.util.StringUtil;
import rx.Observable;
import rx.observables.ConnectableObservable;

import java.util.List;

/**
 * @author Francesco
 */
public abstract class IExtractor extends IPlugin<Message, ExtractionParameters> {
    /**
     * Returns the maximum number of parameters that this extractor supports per each query.
     *
     * @return {@link long} the maximum number of parameters per query.
     */
    public abstract long getMaximumQueryParameters();

    /**
     * Check if the extractor supports regular text queries.
     *
     * @return {@link boolean} true if there is support for text queries.
     */
    public abstract boolean getSupportQuery();

    /**
     * Check if the extractor supports searching with geolocation parameters
     * (usually latitude, longitude, radius).
     *
     * @return {@link boolean} true if there is support for geolocation.
     */
    public abstract boolean getSupportGeoLocation();

    /**
     * Check if the extractor supports searching for messages sent from
     * a specific social network user (implementation-dependent).
     *
     * @return {@link boolean} true if there is support for origin searching.
     */
    public abstract boolean getSupportFrom();

    /**
     * Check if the extractor supports searching for messages sent to
     * a specific social network user (implementation-dependent).
     *
     * @return {@link boolean} true if there is support for destination searching.
     */
    public abstract boolean getSupportTo();

    /**
     * Check if the extractor supports searching for messages that reference
     * a specific social network user (implementation-dependent).
     *
     * @return {@link boolean} true if there is support for reference searching.
     */
    public abstract boolean getSupportReference();

    /**
     * Check if the extractor supports searching for messages sent since a specific date.
     *
     * @return {@link boolean} true if there is support for starting date searching.
     */
    public abstract boolean getSupportSince();

    /**
     * Check if the extractor supports searching for messages sent until a specific date.
     *
     * @return {@link boolean} true if there is support for ending date searching.
     */
    public abstract boolean getSupportUntil();

    /**
     * Check if the extractor supports searching for messages written in a specific
     * language.
     *
     * @return {@link boolean} true if there is support for language searching.
     */
    public abstract boolean getSupportLanguage();

    /**
     * Check if the extractor supports searching for messages written in a specific
     * language locale.
     *
     * @return {@link boolean} true if there is support for language locale searching.
     */
    public abstract boolean getSupportLocale();

    /**
     * Check if the extractor needs the author user OR the recipient user to be specified.
     * Please not this is an inclusive OR.
     *
     * @return {@link boolean} true if the extractor needs the author or the recipient user.
     */
    public abstract boolean mustSpecifyToOrFrom();

    /**
     * Validate some extraction parameters, returning true if they are valid for the current implementation of
     * {@link IExtractor}, or throwing an
     * {@link net.frakbot.crowdpulse.social.exception.SocialException}.
     * IMPORTANT: this is a pre-validation technique, it may rely on obsolete information.
     *
     * @param parameters Some {@link net.frakbot.crowdpulse.social.extraction.ExtractionParameters} set.
     * @return true if the parameters are valid.
     * @throws net.frakbot.crowdpulse.social.exception.SocialException if a parameter is invalid.
     */
    public boolean validateParameters(ExtractionParameters parameters) throws SocialException {
        validateParameter("query", parameters.getQuery(), getSupportQuery());
        validateParameter("geolocation", parameters.getGeoLocationBox(), getSupportGeoLocation());
        validateParameter("from", parameters.getFromUser(), getSupportFrom());
        validateParameter("to", parameters.getToUser(), getSupportTo());
        validateParameter("reference", parameters.getReferenceUsers(), getSupportReference());
        validateParameter("since", parameters.getSince(), getSupportSince());
        validateParameter("until", parameters.getUntil(), getSupportUntil());
        validateParameter("language", parameters.getLanguage(), getSupportLanguage());
        validateParameter("locale", parameters.getLocale(), getSupportLocale());
        if (mustSpecifyToOrFrom() &&
                StringUtil.isNullOrEmpty(parameters.getFromUser()) && StringUtil.isNullOrEmpty(parameters.getToUser())) {
            throw new MissingParametersSocialException("You must specify at least one among \"from\" and \"to\".");
        }
        return true;
    }

    private boolean validateParameter(String parameterName, String parameter, boolean isSupported) throws InvalidParametersSocialException {
        if (!StringUtil.isNullOrEmpty(parameter) && !isSupported) {
            throwErrorForInvalidParameter(parameterName);
        }
        return true;
    }

    private boolean validateParameter(String parameterName, Object parameter, boolean isSupported) throws InvalidParametersSocialException {
        if (parameter != null && !isSupported) {
            throwErrorForInvalidParameter(parameterName);
        }
        return true;
    }

    private boolean validateParameter(String parameterName, List parameter, boolean isSupported) throws InvalidParametersSocialException {
        if (parameter != null && parameter.size() > 0 && !isSupported) {
            throwErrorForInvalidParameter(parameterName);
        }
        return true;
    }

    private void throwErrorForInvalidParameter(String parameterName) throws InvalidParametersSocialException {
        throw new InvalidParametersSocialException(String.format("You cannot specify the \"%s\" parameter.", parameterName));
    }

    /**
     * Starts an asynchronous search loading an {@link rx.Observable} of {@link net.frakbot.crowdpulse.data.entity.Message}
     * that will be populated as results come in.
     *
     * @param parameters {@link net.frakbot.crowdpulse.social.extraction.ExtractionParameters} to search for.
     * @return {@link rx.Observable< net.frakbot.crowdpulse.data.entity.Message >}
     */
    protected abstract ConnectableObservable<Message> getMessages(ExtractionParameters parameters);

    @Override public Observable<Message> process(Observable<Message> stream, ExtractionParameters params) {
        return getMessages(params);
    }
}
