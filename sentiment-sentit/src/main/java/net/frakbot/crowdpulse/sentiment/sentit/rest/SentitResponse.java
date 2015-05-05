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

package net.frakbot.crowdpulse.sentiment.sentit.rest;

import net.frakbot.crowdpulse.data.entity.Message;

import java.util.HashMap;

/**
 * Models a response from the Sentit Web Service in the following example format:
 *
 * <pre>
 * {
 *      "response": "OK",
 *      "results": [
 *          {
 *              "id": "id001",
 *              "subjectivity": "subj",
 *              "polarity":" neg"
 *          },
 *          {
 *              "id": "id002",
 *              "subjectivity": "subj",
 *              "polarity": "pos"
 *          }]
 * }
 * </pre>
 *
 * @author Francesco Pontillo
 */
public class SentitResponse {
    private String response;
    private SentitResultMap results;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public SentitResultMap getResults() {
        return results;
    }

    public void setResults(SentitResultMap results) {
        this.results = results;
    }

    public double getSentimentForMessage(Message message) {
        return results.get(message.getId().toString()).getPolarity();
    }

    public static class SentitResultMap extends HashMap<String, SentitResult> {
        // same as HashMap
    }

    public static class SentitResult {
        private String subjectivity;
        private String polarity;

        public SentitResult(String subjectivity, String polarity) {
            this.subjectivity = subjectivity;
            this.polarity = polarity;
        }

        public String getSubjectivity() {
            return subjectivity;
        }

        public void setSubjectivity(String subjectivity) {
            this.subjectivity = subjectivity;
        }

        public double getPolarity() {
            if (polarity == null) {
                return 0;
            }
            if (polarity.equals("pos")) {
                return 1;
            }
            if (polarity.equals("neg")) {
                return -1;
            }
            return 0;
        }

        public void setPolarity(String polarity) {
            this.polarity = polarity;
        }
    }
}
