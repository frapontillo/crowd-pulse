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

package net.frakbot.crowdpulse.tag.wikipediaminer;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.tag.ITaggerOperator;
import retrofit.RestAdapter;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * @see {@link "http://wikipedia-miner.cms.waikato.ac.nz/services/?wikify"}
 * @author Francesco Pontillo
 */
public class WikipediaMinerTagger extends IPlugin<Message, Void> {
    private final static String TAGGER_NAME = "wikipediaminer";
    private final static String WIKIPEDIA_MINER_ENDPOINT = "http://wikipedia-miner.cms.waikato.ac.nz";
    private final static WikipediaMinerService service;

    static {
        // build the REST client
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(WIKIPEDIA_MINER_ENDPOINT)
                .build();
        service = restAdapter.create(WikipediaMinerService.class);
    }

    @Override public String getName() {
        return TAGGER_NAME;
    }

    @Override protected Observable.Operator<Message, Message> getOperator() {
        return new ITaggerOperator() {
            @Override protected List<Tag> getTagsImpl(String text, String language) {
                // get the tags
                WikifyResponse response;
                List<Tag> tags = new ArrayList<>();
                try {
                    response = service.wikify(text);
                    for (WikifyResponse.DetectedTopic topic : response.getDetectedTopics()) {
                        Tag tag = new Tag();
                        tag.setText(topic.getTitle());
                        tags.add(tag);
                    }
                } catch (Exception ignored) {}

                // publish the tags as a connectable observable
                return tags;
            }
        };
    }
}
