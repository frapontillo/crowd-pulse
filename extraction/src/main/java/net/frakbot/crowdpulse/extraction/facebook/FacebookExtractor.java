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

package net.frakbot.crowdpulse.extraction.facebook;

import net.frakbot.crowdpulse.entity.Message;
import net.frakbot.crowdpulse.extraction.Extractor;
import net.frakbot.crowdpulse.extraction.cli.ExtractionParameters;
import net.frakbot.crowdpulse.extraction.exception.ExtractorException;
import net.frakbot.crowdpulse.extraction.twitter.TwitterExtractorRunner;
import rx.Observable;

/**
 * Extractor implementation for Facebook.
 *
 * @author Francesco Pontillo
 */
public class FacebookExtractor extends Extractor {

    public static final String EXTRACTOR_NAME = "facebook";
    private static FacebookExtractorRunner runner = null;

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

    @Override public boolean getSupportGroup() {
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

    @Override public boolean validateParameters(ExtractionParameters parameters) throws ExtractorException {
        return super.validateParameters(parameters);
    }

    @Override public Observable<Message> getMessages(ExtractionParameters parameters) {
        Observable<Message> messages = null;

        // validate parameters
        try {
            validateParameters(parameters);
        } catch (ExtractorException e) {
            System.err.println(e);
            messages = Observable.empty();
            return messages;
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
