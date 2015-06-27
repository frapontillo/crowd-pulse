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

package net.frakbot.crowdpulse.social.extraction;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.VoidConfig;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;

import java.util.List;
import java.util.Map;

/**
 * Crowd Pulse plugin to fetch message replies.
 *
 * @author Francesco Pontillo
 */
public abstract class IReplyExtractor extends IPlugin<Message, Message, VoidConfig> {

    /**
     * Retrieve the replies for the given {@link Message}.
     * The replies should not include the input message, as it will be automatically emitted before the replies.
     *
     * @param message       The {@link Message} to fetch replies for.
     * @param parameters    The parameters that will be needed to properly convert the replies.
     * @return              A {@link List<Message>} containing all the replies.
     */
    public abstract List<Message> getReplies(Message message, ExtractionParameters parameters);

    @Override protected Observable.Operator<Message, Message> getOperator(VoidConfig parameters) {
        return subscriber -> new CrowdSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                ExtractionParameters newParams = new ExtractionParameters();
                newParams.setSource(getName());
                newParams.setTags(message.getCustomTags());
                List<Message> replies = getReplies(message, newParams);
                subscriber.onNext(message);
                replies.forEach(subscriber::onNext);
            }
        };
    }

    @Override public VoidConfig buildConfiguration(Map<String, String> configurationMap) {
        return new VoidConfig().buildFromMap(configurationMap);
    }

}
