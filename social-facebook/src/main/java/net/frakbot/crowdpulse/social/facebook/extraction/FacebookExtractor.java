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

package net.frakbot.crowdpulse.social.facebook.extraction;

import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.social.extraction.ExtractionParameters;
import net.frakbot.crowdpulse.social.extraction.IExtractor;
import net.frakbot.crowdpulse.social.exception.SocialException;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.observers.SafeSubscriber;

/**
 * IExtractor implementation for Facebook.
 *
 * @author Francesco Pontillo
 */
public class FacebookExtractor extends IExtractor {

    public static final String EXTRACTOR_NAME = "extractor-facebook";
    private static FacebookExtractorRunner runner = null;
    private static final Logger logger = CrowdLogger.getLogger(FacebookExtractor.class);

    @Override public String getName() {
        return EXTRACTOR_NAME;
    }

    @Override public long getMaximumQueryParameters() {
        return -1;
    }

    @Override public boolean getSupportQuery() {
        return true;
    }

    @Override public boolean getSupportGeoLocation() {
        return false;
    }

    @Override public boolean getSupportFrom() {
        return true;
    }

    @Override public boolean getSupportTo() {
        return true;
    }

    @Override public boolean getSupportReference() {
        return true;
    }

    @Override public boolean getSupportSince() {
        return true;
    }

    @Override public boolean getSupportUntil() {
        return true;
    }

    @Override public boolean getSupportLanguage() {
        return false;
    }

    @Override public boolean getSupportLocale() {
        return false;
    }

    @Override public boolean mustSpecifyToOrFrom() {
        return true;
    }

    @Override public boolean validateParameters(ExtractionParameters parameters) throws SocialException {
        return super.validateParameters(parameters);
    }

    @Override public Observable<Message> getMessages(ExtractionParameters parameters) {
        Observable<Message> messages;

        // validate parameters
        try {
            validateParameters(parameters);
        } catch (SocialException e) {
            logger.error(e);
            System.err.println(e);
            messages = Observable.empty();
            return messages.publish();
        }

        return getRunnerInstance().getMessages(parameters);
    }

    private FacebookExtractorRunner getRunnerInstance() {
        if (runner == null) {
            runner = new FacebookExtractorRunner();
        }
        return runner;
    }
}
