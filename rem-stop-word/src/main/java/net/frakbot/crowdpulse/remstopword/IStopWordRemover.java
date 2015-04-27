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

package net.frakbot.crowdpulse.remstopword;

import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class IStopWordRemover {
    /**
     * Returns the name of the stop word remover implementation.
     *
     * @return {@link java.lang.String} the name of the stop word remover.
     */
    public abstract String getName();

    /**
     * Search for stop words among the given {@link Message} {@link Token}s, then marks their {@link Token#isStopWord()}
     * property to true.
     *
     * @param message The {@link Message} containing {@link Token}s.
     * @return The same {@link List} of {@link Token}s of the input {@link Message}, eventually modified.
     */
    public abstract List<Token> stopWordRemoveMessageTokens(Message message);

    /**
     * Search for stop words between all the {@link Token}s of the {@link Message}, then sets the proper
     * {@link Token#isStopWord()} properties to to true.
     *
     * @param message The {@link Message} to process.
     * @return the {@link Message} with optionally marked-as-stop-word {@link Token}s.
     */
    public Message stopWordRemoveMessage(Message message) {
        stopWordRemoveMessageTokens(message);
        return message;
    }
}
