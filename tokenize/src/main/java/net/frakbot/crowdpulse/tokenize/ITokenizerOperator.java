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

package net.frakbot.crowdpulse.tokenize;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class ITokenizerOperator implements Observable.Operator<Message, Message> {
    private IPlugin plugin;

    public ITokenizerOperator(IPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                plugin.reportElementAsStarted(message.getId());
                message = tokenize(message);
                plugin.reportElementAsEnded(message.getId());
                subscriber.onNext(message);
            }

            @Override public void onCompleted() {
                plugin.reportPluginAsCompleted();
                super.onCompleted();
            }

            @Override public void onError(Throwable e) {
                plugin.reportPluginAsErrored();
                super.onError(e);
            }
        };
    }

    /**
     * Tokenize the input {@link Message} into chunks represented by {@link Token}s.
     * No extra information will be stored into the retrieved elements except for the {@link Token#text}.
     *
     * @param message The {@link Message} to process.
     * @return a {@link List} of simple {@link Token}s.
     */
    public abstract List<Token> getTokens(Message message);

    /**
     * Retrieve all the {@link Token}s of the {@link Message}, then sets them to the {@link Message} itself.
     *
     * @param message The {@link Message} to process.
     * @return the {@link Message} with newly added {@link Token}s.
     */
    public Message tokenize(Message message) {
        message.setTokens(getTokens(message));
        return message;
    }

}
