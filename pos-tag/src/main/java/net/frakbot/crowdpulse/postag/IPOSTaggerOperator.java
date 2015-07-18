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

package net.frakbot.crowdpulse.postag;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class IPOSTaggerOperator implements Observable.Operator<Message, Message> {
    
    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = posTagMessage(message);
                subscriber.onNext(message);
            }
        };
    }

    /**
     * Tags all the {@link Token}s of the {@link Message}, then return the input {@link Message}.
     *
     * @param message The {@link Message} to process.
     * @return the {@link Message} with POS-tagged {@link Token}s.
     */
    public Message posTagMessage(Message message) {
        posTagMessageTokens(message);
        return message;
    }

    /**
     * Tags the given {@link Token}s with Part-Of-Speech named entities.
     * @param message The {@link Message} whose {@link List} of {@link Token}s will be modified in order to include the
     *                related Part-Of-Speech tags.
     * @return The same {@link List} of {@link Token}s of the input {@link Message}, eventually modified.
     */
    public abstract List<Token> posTagMessageTokens(Message message);

}
