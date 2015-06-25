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

package net.frakbot.crowdpulse.data.rx;

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import org.apache.logging.log4j.Logger;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class MessagePersister extends IPlugin<Message, Message, MessagePersister.MessagePersisterOptions> {
    public final static String PLUGIN_NAME = "message-persist";
    private final MessageRepository messageRepository;
    private final Logger logger = CrowdLogger.getLogger(MessagePersister.class);

    public MessagePersister() {
        messageRepository = new MessageRepository();
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override protected Observable.Operator<Message, Message> getOperator(MessagePersisterOptions parameters) {
        return subscriber -> new CrowdSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                if (parameters != null) {
                    List<String> tags = new ArrayList<>();
                    tags.add(parameters.getTag());
                    message.setCustomTags(tags);
                }
                messageRepository.save(message);
                subscriber.onNext(message);
            }
        };
    }

    public static class MessagePersisterOptions {
        private String tag;

        public MessagePersisterOptions(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}
