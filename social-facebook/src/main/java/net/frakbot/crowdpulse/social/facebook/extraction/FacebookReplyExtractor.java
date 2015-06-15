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
import facebook4j.FacebookException;
import facebook4j.Reading;
import facebook4j.ResponseList;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.social.extraction.ExtractionParameters;
import net.frakbot.crowdpulse.social.extraction.IReplyExtractor;
import net.frakbot.crowdpulse.social.facebook.FacebookFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class FacebookReplyExtractor extends IReplyExtractor {
    public final static String PLUGIN_NAME = "reply-extractor-facebook";

    @Override public List<Message> getReplies(Message message, ExtractionParameters parameters) {
        Reading reading = new Reading().summary().filter("stream");
        FacebookCommentConverter converter = new FacebookCommentConverter(parameters);
        HashMap<String, Object> map = new HashMap<>();
        map.put(FacebookCommentConverter.DATA_REPLY_TO_COMMENT, message.getoId());
        map.put(FacebookCommentConverter.DATA_REPLY_TO_USER, message.getFromUser());
        map.put(FacebookCommentConverter.DATA_SOURCE, parameters.getSource());
        List<Message> messages = new ArrayList<>();
        String cursor = null;

        try {
            do {
                if (cursor != null) {
                    reading.after(cursor);
                }
                ResponseList<Comment> comments = FacebookFactory.getFacebookInstance().posts().getPostComments(message.getoId(), reading);
                converter.addFromExtractor(comments, messages, map);
                if (comments.getPaging() == null) {
                    cursor = null;
                } else {
                    cursor = comments.getPaging().getCursors().getAfter();
                }
            } while (cursor != null);
        } catch (FacebookException ignored) {}

        return messages;
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }
}
