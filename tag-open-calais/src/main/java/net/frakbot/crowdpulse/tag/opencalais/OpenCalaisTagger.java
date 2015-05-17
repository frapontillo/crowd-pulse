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
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.tag.ITaggerOperator;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * @see {@link "http://www.opencalais.com/documentation/calais-web-service-api/api-invocation/rest"}
 * @author Francesco Pontillo
 */
public class OpenCalaisTagger extends IPlugin<Message> {
    private final static String TAGGER_NAME = "opencalais";
    private final static String OPEN_CALAIS_ENDPOINT = "http://api.opencalais.com/tag/rs";
    private final static OpenCalaisService service;

    static {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OpenCalaisResponse.class, new OpenCalaisResponseAdapter())
                .create();
        // build the REST client
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(OPEN_CALAIS_ENDPOINT)
                .setRequestInterceptor(new OpenCalaisInterceptor())
                .setConverter(new GsonConverter(gson))
                .build();
        service = restAdapter.create(OpenCalaisService.class);
    }

    @Override public String getName() {
        return TAGGER_NAME;
    }

    @Override protected Observable.Operator<Message, Message> getOperator() {
        return new ITaggerOperator() {
            @Override protected List<Tag> getTagsImpl(String text, String language) {
                OpenCalaisResponse response;
                List<Tag> tags = new ArrayList<>();
                try {
                    response = service.tag(text);
                    for (String entity : response.getEntities()) {
                        Tag tag = new Tag();
                        tag.setText(entity);
                        tags.add(tag);
                    }
                } catch (Exception ignored) {}

                // publish the tags as a connectable observable
                return tags;
            }
        };
    }
}
