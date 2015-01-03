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

package net.frakbot.crowdpulse.extraction.facebook;

import facebook4j.IdNameEntity;
import facebook4j.Post;
import facebook4j.Tag;
import net.frakbot.crowdpulse.entity.Message;
import net.frakbot.crowdpulse.extraction.MessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class FacebookMessageConverter extends MessageConverter<Post> {
    @Override public Message fromExtractor(Post original) {
        Message message = new Message();
        message.setSource(FacebookExtractor.EXTRACTOR_NAME);
        message.setText(original.getMessage());
        message.setFromUser(original.getFrom().getId());

        // the "to" user is, by convention, the first user in the "to" list
        List<String> toIds = new ArrayList<String>(original.getTo().size());
        for (IdNameEntity to : original.getTo()) {
            toIds.add(to.getId());
        }
        message.setToUsers(toIds);

        // the creation time is (strangely) not always present, use the updated time info instead
        if (original.getCreatedTime() != null) {
            message.setDate(original.getCreatedTime());
        } else {
            message.setDate(original.getUpdatedTime());
        }

        // convert the referenced users
        List<String> refUsers = new ArrayList<String>(original.getMessageTags().size());
        for (Tag tag : original.getMessageTags()) {
            refUsers.add(tag.getId());
        }
        message.setRefUsers(refUsers);

        return message;
    }
}
