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

import net.frakbot.crowdpulse.data.entity.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class MessageConverter<T> {
    public static final String DATA_REPLY_TO_COMMENT = "DATA_REPLY_TO_COMMENT";
    public static final String DATA_REPLY_TO_USER = "DATA_REPLY_TO_USER";
    public static final String DATA_SOURCE = "DATA_SOURCE";

    private final ExtractionParameters parameters;

    public MessageConverter(ExtractionParameters parameters) {
        this.parameters = parameters;
    }

    protected abstract Message fromSpecificExtractor(T original, HashMap<String, Object> additionalData);

    public Message fromExtractor(T original, HashMap<String, Object> additionalData) {
        Message converted = fromSpecificExtractor(original, additionalData);
        converted.setCustomTags(parameters.getTags());
        converted.setSource(parameters.getSource());
        return converted;
    }

    public Message fromExtractor(T original) {
        Message converted = fromSpecificExtractor(original, null);
        converted.setCustomTags(parameters.getTags());
        converted.setSource(parameters.getSource());
        return converted;
    }

    public List<Message> fromExtractor(List<T> originalList) {
        List<Message> messageList = new ArrayList<>(originalList.size());
        return addFromExtractor(originalList, messageList);
    }

    public <L extends List> List<Message> addFromExtractor(L originalList, List<Message> addToList, HashMap<String, Object> additionalData) {
        for (Object original : originalList) {
            addToList.add(fromExtractor((T) original, additionalData));
        }
        return addToList;
    }

    public <L extends List> List<Message> addFromExtractor(L originalList, List<Message> addToList) {
        return addFromExtractor(originalList, addToList, null);
    }
}
