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
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import org.bson.types.ObjectId;
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
    private MessageRepository messageRepository;

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public MessagePersisterOptions getNewParameter() {
        return new MessagePersisterOptions();
    }

    @Override protected Observable.Operator<Message, Message> getOperator(MessagePersisterOptions parameters) {
        // init the message repository with the given target DB, if any
        messageRepository = new MessageRepository(parameters.getDb());

        return subscriber -> new CrowdSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                ObjectId id = message.getId();
                reportElementAsStarted(id);

                message.setCustomTags(parameters.getTags());
                messageRepository.updateOrInsert(message);

                reportElementAsEnded(id);
                subscriber.onNext(message);
            }

            @Override public void onCompleted() {
                reportPluginAsCompleted();
                super.onCompleted();
            }

            @Override public void onError(Throwable e) {
                reportPluginAsErrored();
                super.onError(e);
            }
        };
    }

    /**
     * Persisting options including the custom tags to persist with the {@link Message} and the database to save to.
     */
    public class MessagePersisterOptions extends GenericDbConfig<MessagePersisterOptions> {
        private List<String> tags;

        /**
         * Get the tags to persist messages with.
         * @return A {@link List} of tags as {@link String}.
         */
        public List<String> getTags() {
            return tags;
        }

        /**
         * Set the tags that will be added to messages before persisting them.
         * @param tags A {@link List} of tags as {@link String}.
         */
        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        @Override public MessagePersisterOptions buildFromJsonElement(JsonElement json) {
            return PluginConfigHelper.buildFromJson(json, MessagePersisterOptions.class);
        }
    }
}
