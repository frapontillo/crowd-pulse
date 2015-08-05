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

import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.IPluginConfig;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import rx.Observable;

import java.util.List;

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

    @Override public MessagePersisterOptions getNewParameter() {
        return new MessagePersisterOptions();
    }

    @Override protected Observable.Operator<Message, Message> getOperator(MessagePersisterOptions parameters) {
        return subscriber -> new CrowdSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                // if the message was already persisted, update its favs and shares
                Message originalMessage = messageRepository.getByOriginalId(message.getoId());
                if (originalMessage != null) {
                    originalMessage.setFavs(message.getFavs());
                    originalMessage.setShares(message.getShares());
                    message = originalMessage;
                }
                if (parameters != null) {
                    message.setCustomTags(parameters.getTags());
                }
                messageRepository.save(message);
                subscriber.onNext(message);
            }
        };
    }

    /**
     * Persisting options including the custom tags to persist with the {@link Message}.
     */
    public static class MessagePersisterOptions implements IPluginConfig<MessagePersisterOptions> {
        private List<String> tags;

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        @Override public MessagePersisterOptions buildFromJsonElement(JsonElement json) {
            return PluginConfigHelper.buildFromJson(json, MessagePersisterOptions.class);
        }
    }
}
