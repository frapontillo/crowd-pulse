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

package net.frakbot.crowdpulse.wikipedia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;
import net.frakbot.crowdpulse.common.util.rx.BackpressureAsyncTransformer;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.repository.TagRepository;
import net.frakbot.crowdpulse.wikipedia.rest.WikipediaResponse;
import net.frakbot.crowdpulse.wikipedia.rest.WikipediaResponseDeserializer;
import net.frakbot.crowdpulse.wikipedia.rest.WikipediaService;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Francesco Pontillo
 */
public class TagCategorizer {
    private final TagRepository tagRepository = new TagRepository();
    private Gson gson;
    private Map<String, WikipediaService> wikipediaServiceMap;
    private final String WIKIPEDIA_ENDPOINT_1 = "http://";
    private final String WIKIPEDIA_ENDPOINT_2 = ".wikipedia.org/w";

    public TagCategorizer() {
        // build the Gson deserializers collection
        gson = new GsonBuilder()
                .registerTypeAdapter(WikipediaResponse.class, new WikipediaResponseDeserializer())
                .create();
        wikipediaServiceMap = new HashMap<String, WikipediaService>();
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

    public ConnectableObservable<Tag> categorizeTags(final GenericAnalysisParameters parameters) {
        Observable<Tag> tags = Observable.create(new Observable.OnSubscribe<Tag>() {
            @Override public void call(Subscriber<? super Tag> subscriber) {
                // read all of the tags
                final List<Tag> allTags = tagRepository.getBetweenKeys(
                        parameters.getFrom(), parameters.getTo());

                // for each tag, get all categories and add them to the collection
                for (Tag tag : allTags) {
                    WikipediaService wikipediaService = getService(tag.getLanguage());
                    tag.addCategories(wikipediaService.tag(tag.getText()).getCategories());
                    subscriber.onNext(tag);
                }

                subscriber.onCompleted();
            }
        }).compose(new BackpressureAsyncTransformer<Tag>());

        return tags.publish();
    }
}
