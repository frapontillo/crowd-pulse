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

package net.frakbot.crowdpulse.tag.tagme;

import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.tag.ITagger;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @see {@link "http://tagme.di.unipi.it/tagme_help.html#tagging"}
 * @author Francesco Pontillo
 */
public class TagMeTagger extends ITagger {
    private final static String TAGGER_NAME = "tagme";

    private final String TAG_ME_ENDPOINT = "http://tagme.di.unipi.it";
    private final List<String> supportedLangs = Arrays.asList("IT", "EN");
    private TagMeService service;

    public TagMeTagger() {
        // build the REST client
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TAG_ME_ENDPOINT)
                .setRequestInterceptor(new TagMeInterceptor())
                .build();
        service = restAdapter.create(TagMeService.class);
    }

    @Override public String getName() {
        return TAGGER_NAME;
    }

    @Override public List<Tag> getTagsImpl(String text, String language) {
        // get the tags
        TagMeResponse response;
        List<Tag> tags = new ArrayList<Tag>();

        if (language != null && supportedLangs.contains(language.toUpperCase())) {
            try {
                response = service.tag(text, language);
                for (TagMeResponse.TagMeAnnotation annotation : response.getAnnotations()) {
                    Tag tag = new Tag();
                    tag.setText(annotation.getTitle());
                    tags.add(tag);
                }
            } catch (RetrofitError e) {
                // ignored
                System.err.println(String.format("%s returned\n%s: %s", e.getUrl(), e.getResponse().getStatus(), e.getResponse().getReason()));
            } catch (Exception e) {
                // ignored
                e.printStackTrace();
            }
        }
        // publish the tags as a connectable observable
        return tags;
    }
}
