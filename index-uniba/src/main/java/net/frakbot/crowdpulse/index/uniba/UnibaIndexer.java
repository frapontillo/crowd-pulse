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

package net.frakbot.crowdpulse.index.uniba;

import net.frakbot.crowdpulse.common.util.ConfigUtil;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.DateUtil;
import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.rx.RxUtil;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.index.uniba.rest.Index;
import net.frakbot.crowdpulse.index.uniba.rest.IndexRequest;
import net.frakbot.crowdpulse.index.uniba.rest.IndexResponse;
import net.frakbot.crowdpulse.index.uniba.rest.UnibaIndexService;
import org.apache.logging.log4j.Logger;
import retrofit.RestAdapter;
import rx.Observable;
import rx.observables.GroupedObservable;

import java.util.*;

/**
 * Plugin that performs message indexing by relying on the Akka Indexing platform
 * built by Alessandro Natilla and Angelo Impedovo.
 *
 * @author Francesco Pontillo
 */
public class UnibaIndexer extends IPlugin<Message, Message, IndexParameters> {
    private static final String PLUGIN_NAME = "index-uniba";
    private static final Logger logger = CrowdLogger.getLogger(UnibaIndexer.class);
    private static final int MAX_BUFFER = 20;
    private static final List<String> SUPPORTED_LANGS = Arrays.asList("it", "en");

    private UnibaIndexService service;
    private String schemaName;

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public IndexParameters buildConfiguration(Map<String, String> configurationMap) {
        return new IndexParameters().buildFromMap(configurationMap);
    }

    @Override protected Observable.Operator<Message, Message> getOperator(IndexParameters parameters) {
        return null;
    }

    @Override public Observable.Transformer<Message, Message> transform(IndexParameters params) {
        return messageObservable -> {
            Observable<Observable<Message>> grouped = messageObservable
                    // group the messages by language
                    // then, for each language, buffer the messages, index them, then flatten them again within the group
                    .groupBy(Message::getLanguage)
                    .lift(subscriber -> new CrowdSubscriber<GroupedObservable<String, Message>>(subscriber) {
                        @Override public void onNext(GroupedObservable<String, Message> group) {
                            Observable<Message> messages = group
                                    .buffer(MAX_BUFFER)
                                    .lift(getBufferedOperator(params, group.getKey()))
                                    .compose(RxUtil.flatten());
                            subscriber.onNext(messages);
                        }
                    });
            // in the end, merge all of the groups
            return Observable.merge(grouped);
        };
    }

    private String getSchemaName(IndexParameters parameters) {
        Index currentIndex = new Index();
        schemaName = "schema-" + DateUtil.getUnixEpoch(new Date());
        currentIndex.setId(schemaName);
        currentIndex.setSchema(parameters.getSchema());
        IndexResponse response = getService().createIndex(currentIndex);
        // if the creation fails, notice self
        if (response.hasErrored()) {
            logger.error(String.format("Could not create an indexing schema (\"%s\").", response.getError()));
        }
        return schemaName;
    }

    private Observable.Operator<List<Message>, List<Message>> getBufferedOperator(IndexParameters parameters,
            String language) {

        // if the language is supported, index the contents
        if (SUPPORTED_LANGS.contains(language)) {
            return subscriber -> new CrowdSubscriber<List<Message>>(subscriber) {

                @Override public void onNext(List<Message> messages) {
                    // feed the created model with the new message
                    IndexRequest req = new IndexRequest();
                    req.setId(getSchemaName(parameters));
                    req.setLang(language);
                    req.setContents(messages);

                    // make the request
                    IndexResponse res = getService().index(req);
                    // if the response errors, let the pipeline handle it
                    if (res.hasErrored()) {
                        logger.error(String.format("Could not index message (\"%s\").", res.getError()));
                    }
                    subscriber.onNext(messages);
                }

            };
        }

        // if the language is not supported, flow elements through
        return subscriber -> new CrowdSubscriber<List<Message>>(subscriber) {
            @Override public void onNext(List<Message> messages) {
                subscriber.onNext(messages);
            }
        };
    }

    private UnibaIndexService getService() {
        if (service == null) {
            Properties props = ConfigUtil.getPropertyFile(this.getClass(), "index.properties");
            String baseUrl = props.getProperty("index.base");
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(baseUrl)
                    .build();
            service = restAdapter.create(UnibaIndexService.class);
        }
        return service;
    }
}
