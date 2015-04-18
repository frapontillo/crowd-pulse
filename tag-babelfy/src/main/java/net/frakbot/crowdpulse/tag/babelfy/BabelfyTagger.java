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

package net.frakbot.crowdpulse.tag.babelfy;

import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.tag.ITagger;
import retrofit.RestAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class BabelfyTagger extends ITagger {
    private final static String TAGGER_NAME = "babelfy";

    private final String BABELFY_ENDPOINT = "http://babelfy.org";

    @Override public String getName() {
        return TAGGER_NAME;
    }

    @Override protected List<Tag> getTagsImpl(String text, String language) {
        // build the REST client
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BABELFY_ENDPOINT)
                .setRequestInterceptor(new BabelfyInterceptor())
                .build();
        BabelfyService service = restAdapter.create(BabelfyService.class);

        BabelfyResponse response;

        List<Tag> tags = new ArrayList<Tag>();
        try {
            response = service.tag(text, language.toUpperCase());
            for (String annotation : response.getTags()) {
                Tag tag = new Tag();
                tag.setText(annotation);
                tags.add(tag);
            }
        } catch (Exception e) {
            // ignored
        }

        // publish the tags as a connectable observable
        return tags;
    }
}
