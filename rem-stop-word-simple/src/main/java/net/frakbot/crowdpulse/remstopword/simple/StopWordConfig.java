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

package net.frakbot.crowdpulse.remstopword.simple;

import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.spi.IPluginConfig;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for the stop word removal step.
 *
 * @author Francesco Pontillo
 */
public class StopWordConfig implements IPluginConfig<StopWordConfig> {
    public final static String APPLY_TO_TOKENS = "tokens";
    public final static String APPLY_TO_TAGS = "tags";
    public final static String APPLY_TO_CATEGORIES = "categories";

    private List<String> applyTo;
    private StopWordDictionaries dictionaries;

    public List<String> getApplyTo() {
        return applyTo;
    }

    public void setApplyTo(List<String> applyTo) {
        this.applyTo = applyTo;
    }

    public StopWordDictionaries getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(StopWordDictionaries dictionaries) {
        this.dictionaries = dictionaries;
    }

    public boolean mustStopTokens() {
        return (applyTo != null && applyTo.contains(APPLY_TO_TOKENS));
    }

    public boolean mustStopTags() {
        return (applyTo != null && applyTo.contains(APPLY_TO_TAGS));
    }

    public boolean mustStopCategories() {
        return (applyTo != null && applyTo.contains(APPLY_TO_CATEGORIES));
    }

    @Override public StopWordConfig buildFromJsonElement(JsonElement json) {
        StopWordConfig config = PluginConfigHelper.buildFromJson(json, StopWordConfig.class);
        if (config.getApplyTo() == null || config.getApplyTo().size() == 0) {
            config.setApplyTo(Arrays.asList(APPLY_TO_TOKENS, APPLY_TO_TAGS, APPLY_TO_CATEGORIES));
        }
        return config;
    }

    public class StopWordDictionaries {
        private List<String> all;
        private List<String> tokens;
        private List<String> tags;
        private List<String> categories;

        public List<String> getAll() {
            return all;
        }

        public void setAll(List<String> all) {
            this.all = all;
        }

        public List<String> getTokens() {
            return tokens;
        }

        public void setTokens(List<String> tokens) {
            this.tokens = tokens;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
    }
}
