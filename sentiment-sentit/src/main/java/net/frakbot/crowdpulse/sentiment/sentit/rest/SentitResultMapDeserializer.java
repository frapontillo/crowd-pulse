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

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author Francesco Pontillo
 */
public class SentitResultMapDeserializer implements JsonDeserializer<SentitResponse.SentitResultMap> {
    @Override
    public SentitResponse.SentitResultMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext
            context) throws JsonParseException {
        SentitResponse.SentitResultMap map = new SentitResponse.SentitResultMap();
        JsonArray array = json.getAsJsonArray();
        array.forEach(elem -> {
            JsonObject obj = elem.getAsJsonObject();
            map.put(obj.get("id").getAsString(), new SentitResponse.SentitResult(
                    obj.get("subjectivity").getAsString(),
                    obj.get("polarity").getAsString()));
        });
        return map;
    }
}
