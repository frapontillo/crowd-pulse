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

import com.restfb.types.Post;
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
        // TODO: get the optional recipient user
        message.setToUser(null);
        message.setDate(original.getCreatedTime());

        // convert the referenced users
        List<String> refUsers = new ArrayList<String>(original.getMessageTags().size());
        for (List<Post.MessageTag> tagList : original.getMessageTags().values()) {
            for (Post.MessageTag tag : tagList) {
                refUsers.add(tag.getId());
            }
        }
        message.setRefUsers(refUsers);

        return message;
    }
}
