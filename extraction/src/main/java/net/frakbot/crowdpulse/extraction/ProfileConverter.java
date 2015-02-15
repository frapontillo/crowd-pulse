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

package net.frakbot.crowdpulse.extraction;

import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.extraction.cli.ProfileParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class ProfileConverter<T> {
    private final ProfileParameters parameters;

    public ProfileConverter(ProfileParameters parameters) {
        this.parameters = parameters;
    }

    protected abstract Profile fromSpecificExtractor(T original, HashMap<String, Object> additionalData);

    public Profile fromExtractor(T original, HashMap<String, Object> additionalData) {
        Profile converted = fromSpecificExtractor(original, additionalData);
        converted.setSource(parameters.getSource());
        converted.setTags(parameters.getTags());
        return converted;
    }

    public List<Profile> fromExtractor(List<T> originalList) {
        List<Profile> messageList = new ArrayList<Profile>(originalList.size());
        for (T original : originalList) {
            messageList.add(fromExtractor(original, null));
        }
        return messageList;
    }
}
