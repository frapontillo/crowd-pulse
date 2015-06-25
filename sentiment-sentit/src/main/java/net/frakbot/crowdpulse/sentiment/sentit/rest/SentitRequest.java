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

import java.util.ArrayList;
import java.util.List;

/**
 * Models a Sentit Web Service request in the following example format:
 *
 * <pre>
 * {
 *      "texts": [
 *          {
 *              "id": "id001",
 *              "text": "Grillo Mi fa paura la gente che urla. Ne abbiamo già visti almeno un paio,
 *                      ed è finita com'è finita. Niente urla per me, grazie."
 *          },
 *          {
 *              "id": "id002",
 *              "text": "@Ale__Malik oddio quanto ti capisco.<3 *--*"
 *          }]
 * }
 * </pre>
 *
 * @author Francesco Pontillo
 */
public class SentitRequest {
    private List<SentitText> texts;

    public SentitRequest() {
        texts = new ArrayList<>();
    }

    public SentitRequest(List<Message> messages) {
        texts = new ArrayList<>(messages.size());
        messages.forEach(message -> texts.add(new SentitText(message)));
    }

    public List<SentitText> getTexts() {
        return texts;
    }

    public void setTexts(List<SentitText> texts) {
        this.texts = texts;
    }

    public class SentitText {
        private String id;
        private String text;

        public SentitText(Message message) {
            id = message.getId().toString();
            text = message.getText();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
