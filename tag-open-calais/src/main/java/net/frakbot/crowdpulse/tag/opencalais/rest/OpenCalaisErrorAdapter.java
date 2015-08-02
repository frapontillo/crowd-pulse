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

package net.frakbot.crowdpulse.tag.opencalais.rest;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * JSON converter from OpenCalais error to a {@link OpenCalaisError} class.
 *
 * @author Francesco Pontillo
 */
public class OpenCalaisErrorAdapter implements JsonDeserializer<OpenCalaisError> {
    @Override public OpenCalaisError deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        OpenCalaisError error = new OpenCalaisError();

        if (json.getAsJsonObject().get("error") != null) {
            error.setError(json.getAsJsonObject()
                    .getAsJsonObject("error").getAsJsonObject("status").get("errorCode").getAsString());
        }

        return error;
    }
}
