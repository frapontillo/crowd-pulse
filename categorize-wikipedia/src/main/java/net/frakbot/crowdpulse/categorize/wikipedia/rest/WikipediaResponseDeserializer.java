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

package net.frakbot.crowdpulse.categorize.wikipedia.rest;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * @author Francesco Pontillo
 */
public class WikipediaResponseDeserializer implements JsonDeserializer<WikipediaResponse> {

    @Override
    public WikipediaResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        WikipediaResponse response = new WikipediaResponse();

        // get the main query object
        JsonElement queryElement = json.getAsJsonObject().get("query");
        if (queryElement == null) {
            return response;
        }
        // get the pages set
        Set<Map.Entry<String, JsonElement>> pages =
                queryElement.getAsJsonObject().get("pages").getAsJsonObject().entrySet();

        // for each page, retrieve and add all categories
        for (Map.Entry<String, JsonElement> page : pages) {
            if (page.getKey().equals("-1")) {
                continue;
            }
            JsonElement categories = page.getValue().getAsJsonObject().get("categories");
            if (categories != null) {
                JsonArray jsonCategories = categories.getAsJsonArray();
                for (JsonElement jsonCategory : jsonCategories) {
                    response.getCategories().add(jsonCategory.getAsJsonObject().get("title").getAsString());
                }
            }
        }

        return response;
    }
}