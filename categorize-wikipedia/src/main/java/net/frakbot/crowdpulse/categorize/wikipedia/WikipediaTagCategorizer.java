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

package net.frakbot.crowdpulse.categorize.wikipedia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.frakbot.crowdpulse.categorize.ITagCategorizerOperator;
import net.frakbot.crowdpulse.categorize.wikipedia.rest.WikipediaResponse;
import net.frakbot.crowdpulse.categorize.wikipedia.rest.WikipediaResponseDeserializer;
import net.frakbot.crowdpulse.categorize.wikipedia.rest.WikipediaService;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Tag;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Francesco Pontillo
 */
public class WikipediaTagCategorizer extends IPlugin<Tag> {
    public static final String TAGCATEGORIZER_IMPL = "wikipedia";
    private static final String WIKIPEDIA_ENDPOINT_1 = "http://";
    private static final String WIKIPEDIA_ENDPOINT_2 = ".wikipedia.org/w";

    private Gson gson;
    private Map<String, WikipediaService> wikipediaServiceMap;

    public WikipediaTagCategorizer() {
        // build the Gson deserializers collection
        gson = new GsonBuilder()
                .registerTypeAdapter(WikipediaResponse.class, new WikipediaResponseDeserializer())
                .create();
        wikipediaServiceMap = new HashMap<>();
    }

    @Override public String getName() {
        return TAGCATEGORIZER_IMPL;
    }

    @Override public Observable.Operator<Tag, Tag> getOperator() {
        return new ITagCategorizerOperator() {
            @Override public List<String> getCategories(Tag tag) {
                WikipediaService wikipediaService = getService(tag.getLanguage());
                return wikipediaService.tag(tag.getText()).getCategories();
            }
        };
    }

    private WikipediaService getService(String language) {
        WikipediaService wikipediaService = wikipediaServiceMap.get(language);
        if (wikipediaService == null) {
             // build the REST client
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(WIKIPEDIA_ENDPOINT_1 + language + WIKIPEDIA_ENDPOINT_2)
                    .setConverter(new GsonConverter(gson))
                    .build();
            wikipediaService = restAdapter.create(WikipediaService.class);
            wikipediaServiceMap.put(language, wikipediaService);
        }
        return wikipediaService;
    }
}
