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
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.VoidConfig;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.tag.ITaggerOperator;
import net.frakbot.crowdpulse.tag.opencalais.exception.OpenCalaisAPILimitReachedException;
import net.frakbot.crowdpulse.tag.opencalais.exception.OpenCalaisUnsupportedLanguageException;
import net.frakbot.crowdpulse.tag.opencalais.rest.*;
import org.apache.logging.log4j.Logger;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tagging implementation that relies on Thomson Reuters OpenCalais.
 *
 * @author Francesco Pontillo
 * @see {@link "http://developer.permid.org/wp-content/uploads/2015/04/Thomson-Reuters-Open-Calais-API-User-Guide.pdf"}
 */
public class OpenCalaisTagger extends IPlugin<Message, Message, VoidConfig> {
    public final static String PLUGIN_NAME = "opencalais";
    private final static String OPEN_CALAIS_ENDPOINT = "https://api.thomsonreuters.com";
    private final static Logger logger = CrowdLogger.getLogger(OpenCalaisTagger.class);

    private OpenCalaisService service;

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig buildConfiguration(Map<String, String> configurationMap) {
        return new VoidConfig().buildFromMap(configurationMap);
    }

    @Override protected Observable.Operator<Message, Message> getOperator(VoidConfig parameters) {
        return new ITaggerOperator() {
            @Override protected List<Tag> getTagsImpl(String text, String language) {
                OpenCalaisResponse response;
                List<Tag> tags = new ArrayList<>();
                boolean retry;

                do {
                    retry = false;
                    try {
                        response = getService().tag(text, getOpenCalaisLanguage(language));
                        for (String entity : response.getEntities()) {
                            Tag tag = new Tag();
                            tag.setText(entity);
                            tag.addSource(getName());
                            tags.add(tag);
                        }
                    } catch (Exception e) {
                        // if the error has a cause and it must be manually handled
                        if (e.getCause() != null) {
                            // unsupported language is OK to flow
                            if (e.getCause().getClass().equals(OpenCalaisUnsupportedLanguageException.class)) {
                                // flow through with no warnings
                            }
                            // if the error is related to API limit, retry after a couple of seconds
                            if (e.getCause().getClass().equals(OpenCalaisAPILimitReachedException.class)) {
                                logger.info("OpenCalais API limit has been reached, waiting for a couple of seconds...");
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException ignored) {
                                }
                                logger.info("Retrying to call the OpenCalais Web Service...");
                                retry = true;
                            }
                        } else {
                            logger.error(e);
                        }
                    }
                } while (retry);

                // publish the tags as a connectable observable
                return tags;
            }
        };
    }

    private OpenCalaisService getService() {
        if (service == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(OpenCalaisResponse.class, new OpenCalaisResponseAdapter())
                    .create();
            // build the REST client
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(OPEN_CALAIS_ENDPOINT)
                    .setRequestInterceptor(new OpenCalaisInterceptor())
                    .setErrorHandler(new OpenCalaisErrorHandler())
                    .setConverter(new GsonConverter(gson))
                    .build();
            service = restAdapter.create(OpenCalaisService.class);
        }
        return service;
    }

    /**
     * Transform the Crowd Pulse representation of languages into the one accepted by OpenCalais.
     * If the input language isn't supported by OpenCalais (support is for English, Spanish, French only), then a null
     * value will be passed on to Open Calais, which will then determine the language itself.
     *
     * @param crowdPulseLanguage The input language to transform.
     * @return A {@link String} representation of the language as accepted by OpenCalais.
     */
    private String getOpenCalaisLanguage(String crowdPulseLanguage) {
        switch (crowdPulseLanguage) {
            case "en":
                return "English";
            case "es":
                return "Spanish";
            case "fr":
                return "French";
            default:
                return null;
        }
    }
}
