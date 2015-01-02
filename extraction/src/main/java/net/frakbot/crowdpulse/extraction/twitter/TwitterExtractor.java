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

package net.frakbot.crowdpulse.extraction.twitter;

import net.frakbot.crowdpulse.entity.Message;
import net.frakbot.crowdpulse.extraction.Extractor;
import net.frakbot.crowdpulse.extraction.cli.ExtractionParameters;
import net.frakbot.crowdpulse.extraction.exception.ExtractorException;
import net.frakbot.crowdpulse.extraction.exception.InvalidParametersExtractorException;
import net.frakbot.crowdpulse.extraction.exception.TooComplexParametersExtractorException;
import rx.Observable;

/**
 * @author Francesco Pontillo
 */
public class TwitterExtractor extends Extractor {

    public static final String EXTRACTOR_NAME = "twitter";
    private static TwitterExtractorRunner runner = null;

    @Override public String getName() {
        return EXTRACTOR_NAME;
    }

    @Override public long getMaximumQueryParameters() {
        return 10;
    }

    @Override public boolean getSupportQuery() {
        return true;
    }

    @Override public boolean getSupportGeoLocation() {
        return true;
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
        return true;
    }

    @Override public boolean getSupportLocale() {
        return true;
    }

    @Override public boolean mustSpecifyToOrFrom() {
        return false;
    }

    @Override
    public boolean validateParameters(ExtractionParameters parameters) throws ExtractorException {
        super.validateParameters(parameters);
        long paramCount = countNotNullParameters(parameters);
        long maxParamCount = getMaximumQueryParameters();
        if (paramCount > maxParamCount) {
            throw new TooComplexParametersExtractorException(maxParamCount, paramCount);
        }
        if (parameters.getGeoArea() != null && parameters.getGeoLocationBox() != null) {
            throw new InvalidParametersExtractorException("You can only use one of geo-area or geo-location box.");
        }
        return true;
    }

    private int countNotNullParameters(ExtractionParameters parameters) {
        int count = 0;
        if (parameters.getQuery() != null) {
            count = +1;
        }
        if (parameters.getGeoLocationBox() != null) {
            count += 1;
        }
        if (parameters.getFromUser() != null) {
            count += 1;
        }
        if (parameters.getToUser() != null) {
            count += 1;
        }
        if (parameters.getReferenceUsers() != null) {
            count += parameters.getReferenceUsers().size();
        }
        if (parameters.getFromUser() != null) {
            count += 1;
        }
        if (parameters.getSince() != null) {
            count += 1;
        }
        if (parameters.getUntil() != null) {
            count += 1;
        }
        if (parameters.getLanguage() != null) {
            count += 1;
        }
        if (parameters.getLocale() != null) {
            count += 1;
        }
        return count;
    }

    private TwitterExtractorRunner getRunnerInstance() {
        if (runner == null) {
            runner = new TwitterExtractorRunner();
        }
        return runner;
    }

    @Override
    public Observable<Message> getMessages(final ExtractionParameters parameters) {
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
}
