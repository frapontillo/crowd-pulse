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

import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.tag.ITagger;
import retrofit.RestAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @see {@link "http://wikipedia-miner.cms.waikato.ac.nz/services/?wikify"}
 * @author Francesco Pontillo
 */
public class WikipediaMinerTagger extends ITagger {
    private final static String TAGGER_NAME = "wikipediaminer";

    private final String WIKIPEDIA_MINER_ENDPOINT = "http://wikipedia-miner.cms.waikato.ac.nz";

    @Override public String getName() {
        return TAGGER_NAME;
    }

    @Override public List<Tag> getTagsImpl(String text, String language) {
        // build the REST client
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(WIKIPEDIA_MINER_ENDPOINT)
                .build();
        WikipediaMinerService service = restAdapter.create(WikipediaMinerService.class);

        // get the tags
        WikifyResponse response;
        List<Tag> tags = new ArrayList<Tag>();
        try {
            response = service.wikify(text);
            for (WikifyResponse.DetectedTopic topic : response.getDetectedTopics()) {
                Tag tag = new Tag();
                tag.setText(topic.getTitle());
                tags.add(tag);
            }
        } catch (Exception e) {
            // ignored
        }

        // publish the tags as a connectable observable
        return tags;
    }
}
