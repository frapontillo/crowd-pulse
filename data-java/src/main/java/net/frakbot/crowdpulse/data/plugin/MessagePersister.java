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

package net.frakbot.crowdpulse.data.plugin;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.IPluginConfig;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * An implementation of {@link IPlugin} that persists all streamed {@link Message}s with a custom tags defined in its
 * initialization options, eventually completing or erroring.
 *
 * @author Francesco Pontillo
 */
public class MessagePersister extends IPlugin<Message, Message, MessagePersister.MessagePersisterOptions> {
    public final static String PLUGIN_NAME = "message-persist";
    private final MessageRepository messageRepository;

    public MessagePersister() {
        messageRepository = new MessageRepository();
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public MessagePersisterOptions buildConfiguration(Map<String, String> configurationMap) {
        return new MessagePersisterOptions().buildFromMap(configurationMap);
    }

    @Override protected Observable.Operator<Message, Message> getOperator(MessagePersisterOptions parameters) {
        return subscriber -> new CrowdSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                if (parameters != null && parameters.getTags() != null) {
                    message.setCustomTags(Arrays.asList(parameters.getTags().split(",")));
                }
                messageRepository.save(message);
                subscriber.onNext(message);
            }
        };
    }

    /**
     * Persisting options including the custom tags to persist with the {@link Message}.
     */
    public static class MessagePersisterOptions implements IPluginConfig {
        private String tags;

        /**
         * Init persisting options with the custom tags to be persisted with the {@link Message}.
         *
         * @param tags The custom tags to persist (multiple tags must be concatenated with a comma).
         */
        public MessagePersisterOptions(String tags) {
            this.tags = tags;
        }

        public MessagePersisterOptions() {
        }

        /**
         * Get the custom tags to be persisted with the {@link Message}.
         *
         * @return The custom tags to persist (multiple tags are concatenated with a comma).
         */
        public String getTags() {
            return tags;
        }

        /**
         * Set the custom tags to be persisted with the {@link Message}.
         *
         * @param tags The custom tags to persist (multiple tags must be concatenated with a comma).
         */
        public void setTags(String tags) {
            this.tags = tags;
        }

        @Override public MessagePersisterOptions buildFromMap(Map<String, String> mapConfig) {
            if (mapConfig != null) {
                this.setTags(mapConfig.get("tags"));
            }
            return this;
        }
    }
}
