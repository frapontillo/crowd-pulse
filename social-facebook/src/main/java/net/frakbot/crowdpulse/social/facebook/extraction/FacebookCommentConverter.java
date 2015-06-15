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

package net.frakbot.crowdpulse.social.facebook.extraction;

import facebook4j.Comment;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.social.extraction.ExtractionParameters;
import net.frakbot.crowdpulse.social.extraction.MessageConverter;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Francesco Pontillo
 */
public class FacebookCommentConverter extends MessageConverter<Comment> {

    public FacebookCommentConverter(ExtractionParameters parameters) {
        super(parameters);
    }

    @Override public Message fromSpecificExtractor(Comment original, HashMap<String, Object> additionalData) {
        Message message = new Message();
        message.setoId(original.getId());
        message.setText(original.getMessage());
        message.setFromUser(original.getFrom().getId());

        // if the current message is a comment to another message, set its parent here
        if (additionalData != null) {
            String parent = (String) additionalData.get(DATA_REPLY_TO_COMMENT);
            if (parent != null) {
                message.setParent(parent);
            }
            message.setToUsers(Arrays.asList((String) additionalData.get(DATA_REPLY_TO_USER)));
        }

        message.setDate(original.getCreatedTime());

        // TODO: implement proper likes.summary(true) and likes.summary.total_count fetching
        message.setFavs(original.getLikeCount());

        return message;
    }
}
