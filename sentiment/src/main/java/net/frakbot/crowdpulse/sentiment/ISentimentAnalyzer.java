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

package net.frakbot.crowdpulse.sentiment;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;

/**
 * @author Francesco Pontillo
 */
public abstract class ISentimentAnalyzer implements IPlugin {
    /**
     * Retrieve the sentiment values of the {@link Message} {link Observable} and sets them into the respective
     * {@link Message#sentiment} fields.
     *
     * @param messages The {@link Observable<Message>} to process.
     * @return the input {@link Observable<Message>} with the sentiment optionally set.
     */
    public abstract Observable<Message> sentimentAnalyze(Observable<Message> messages);
}
