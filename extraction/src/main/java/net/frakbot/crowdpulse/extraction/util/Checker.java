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

package net.frakbot.crowdpulse.extraction.util;

import net.frakbot.crowdpulse.entity.Message;
import net.frakbot.crowdpulse.extraction.cli.ExtractionParameters;
import rx.functions.Func1;

/**
 * @author Francesco Pontillo
 */
public class Checker {
    public static Func1<Message, Boolean> checkQuery(final ExtractionParameters parameters) {
        return new Func1<Message, Boolean>() {
            @Override public Boolean call(Message message) {
                return (StringUtil.isNullOrEmpty(parameters.getQuery()) || message.getText().contains(parameters.getQuery()));
            }
        };
    }

    public static Func1<Message, Boolean> checkFromUser(final ExtractionParameters parameters) {
        return new Func1<Message, Boolean>() {
            @Override public Boolean call(Message message) {
                return (StringUtil.isNullOrEmpty(parameters.getFromUser()) || parameters.getFromUser().equals(message
                        .getFromUser()));
            }
        };
    }

    public static Func1<Message, Boolean> checkToUser(final ExtractionParameters parameters) {
        return new Func1<Message, Boolean>() {
            @Override public Boolean call(Message message) {
                return (StringUtil.isNullOrEmpty(parameters.getToUser()) || parameters.getToUser().equals(message
                        .getToUser()));
            }
        };
    }

    public static Func1<Message, Boolean> checkReferencedUsers(final ExtractionParameters parameters) {
        return new Func1<Message, Boolean>() {
            @Override public Boolean call(Message message) {
                // if no referenced users are requested
                if (parameters.getReferenceUsers() == null || parameters.getReferenceUsers().size() <= 0) {
                    return true;
                }
                // for each ref user to check
                for (String user : parameters.getReferenceUsers()) {
                    // if the message does not contain it, return false
                    if (!message.getRefUsers().contains(user)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static Func1<Message, Boolean> checkUntilDate(final ExtractionParameters parameters) {
        return new Func1<Message, Boolean>() {
            @Override public Boolean call(Message message) {
                return message.getDate().compareTo(parameters.getUntil()) <= 0;
            }
        };
    }
}
