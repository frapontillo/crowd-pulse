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

package net.frakbot.crowdpulse.tag.opencalais;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.tag.ITagger;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @see {@link "http://www.opencalais.com/documentation/calais-web-service-api/api-invocation/rest"}
 * @author Francesco Pontillo
 */
public class OpenCalaisTagger implements ITagger {
    private final static String TAGGER_NAME = "opencalais";

    private final String OPEN_CALAIS_ENDPOINT = "http://api.opencalais.com/tag/rs";

    @Override public String getName() {
        return TAGGER_NAME;
    }

    @Override public List<Tag> getTags(String text, String language) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OpenCalaisResponse.class, new OpenCalaisResponseAdapter())
                .create();
        // build the REST client
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(OPEN_CALAIS_ENDPOINT)
                .setRequestInterceptor(new OpenCalaisInterceptor())
                .setConverter(new GsonConverter(gson))
                .build();
        OpenCalaisService service = restAdapter.create(OpenCalaisService.class);

        OpenCalaisResponse response;

        List<Tag> tags = new ArrayList<Tag>();
        try {
            response = service.tag(text);
            for (String entity : response.getEntities()) {
                Tag tag = new Tag();
                tag.setText(entity);
                tags.add(tag);
            }
        } catch (Exception e) {
            // ignored
        }

        // publish the tags as a connectable observable
        return tags;
    }
}
