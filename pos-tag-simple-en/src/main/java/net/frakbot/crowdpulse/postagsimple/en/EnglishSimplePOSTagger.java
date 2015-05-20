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

package net.frakbot.crowdpulse.postagsimple.en;

import net.frakbot.crowdpulse.common.util.spi.ISingleablePlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.postagsimple.ISimplePOSTaggerOperator;
import rx.Observable;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class EnglishSimplePOSTagger extends ISingleablePlugin<Message, Void> {
    private final static String SIMPLEPOSTAGGER_IMPL = "simplepostagger-en";

    @Override public String getName() {
        return SIMPLEPOSTAGGER_IMPL;
    }

    @Override public Observable.Operator<Message, Message> getOperator(Void parameters) {
        EnglishSimplePOSTagger actualTagger = this;
        return new ISimplePOSTaggerOperator() {
            @Override public List<Token> posTagMessageTokens(Message message) {
                return actualTagger.singleProcess(message).getTokens();
            }
        };
    }

    @Override public Message singleProcess(Message message) {
        if (message.getTokens() == null) {
            return null;
        }

        // associate each POS with the corresponding Token
        for (Token token : message.getTokens()) {
            String simplePos = null;
            if (token.getPos().startsWith("NN")) {
                simplePos = "n";
            } else if (token.getPos().startsWith("VB")) {
                simplePos = "v";
            } else if (token.getPos().startsWith("JJ")) {
                simplePos = "a";
            } else if (token.getPos().startsWith("RB")) {
                simplePos = "r";
            }
            token.setSimplePos(simplePos);
        }

        return message;
    }
}
