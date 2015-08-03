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

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.VoidConfig;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.tag.ITaggerOperator;
import retrofit.RestAdapter;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class BabelfyTagger extends IPlugin<Message, Message, VoidConfig> {
    public final static String PLUGIN_NAME = "babelfy";
    private final static String BABELFY_ENDPOINT = "http://babelfy.io/v1";
    private static BabelfyService service;

    private BabelfyService getService() {
        if (service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BABELFY_ENDPOINT)
                    .setRequestInterceptor(new BabelfyInterceptor())
                    .build();
            service = restAdapter.create(BabelfyService.class);
        }
        return service;
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig getNewParameter() {
        return new VoidConfig();
    }

    @Override protected Observable.Operator<Message, Message> getOperator(VoidConfig parameters) {
        return new ITaggerOperator() {
            @Override protected List<Tag> getTagsImpl(String text, String language) {
                BabelfyResponse response;
                List<Tag> tags = new ArrayList<>();
                try {
                    response = getService().tag(text, language != null ? language.toUpperCase() : null);
                    for (String annotation : response.getTags(text)) {
                        Tag tag = new Tag();
                        tag.setText(annotation);
                        tag.addSource(getName());
                        tags.add(tag);
                    }
                } catch (Exception e) {
                    // ignored
                    e.printStackTrace();
                    System.err.println(e);
                }

                // publish the tags as a connectable observable
                return tags;
            }
        };
    }
}
