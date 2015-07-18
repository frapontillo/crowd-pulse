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

package net.frakbot.crowdpulse.detectlanguage;

import net.frakbot.crowdpulse.common.util.StringUtil;
import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * Rx {@link rx.Observable.Operator} for categorizing the tags inside a {@link Message}.
 *
 * @author Francesco Pontillo
 */
public abstract class ILanguageDetectorOperator implements Observable.Operator<Message, Message> {

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                if (StringUtil.isNullOrEmpty(message.getLanguage())) {
                    message.setLanguage(getLanguage(message));
                }
                subscriber.onNext(message);
            }
        };
    }

    /**
     * Actual language retrieval for the given {@link Message}.
     *
     * @param message The {@link Message} to process.
     * @return A ISO-compliant {@link String} representing the most probable language for the {@link Message}.
     */
    public abstract String getLanguage(Message message);

}
