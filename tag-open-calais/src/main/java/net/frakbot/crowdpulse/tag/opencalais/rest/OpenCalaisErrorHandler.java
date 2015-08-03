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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.frakbot.crowdpulse.tag.opencalais.exception.OpenCalaisAPILimitReachedException;
import net.frakbot.crowdpulse.tag.opencalais.exception.OpenCalaisUnsupportedLanguageException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Custom OpenCalais error handler.
 *
 * @author Francesco Pontillo
 */
public class OpenCalaisErrorHandler implements ErrorHandler {
    private final List<String> FLOW_THROUGH_LANG_ERRORS = Arrays.asList("Unsupported-Language", "Unrecognized-Language");

    @Override public Throwable handleError(RetrofitError cause) {
        if (cause.getResponse() != null && cause.getResponse().getStatus() == 429) {
            return new OpenCalaisAPILimitReachedException(cause);
        }

        Response r = cause.getResponse();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OpenCalaisError.class, new OpenCalaisErrorAdapter())
                .create();
        GsonConverter converter = new GsonConverter(gson);

        try {
            OpenCalaisError error = (OpenCalaisError) converter.fromBody(r.getBody(), OpenCalaisError.class);
            if (FLOW_THROUGH_LANG_ERRORS.contains(error.getError())) {
                return new OpenCalaisUnsupportedLanguageException(cause);
            }
        } catch (ConversionException ignored) {
        }

        return cause;
    }
}
