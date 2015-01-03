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

package net.frakbot.crowdpulse.extraction.twitter;

import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.extraction.MessageConverter;
import net.frakbot.crowdpulse.extraction.util.StringUtil;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class TwitterMessageConverter extends MessageConverter<Status> {

    @Override public Message fromExtractor(Status original) {
        Message message = new Message();
        message.setSource(TwitterExtractor.EXTRACTOR_NAME);
        message.setText(original.getText());
        message.setFromUser(original.getUser().getScreenName());
        if (!StringUtil.isNullOrEmpty(original.getInReplyToScreenName())) {
            List<String> toIds = new ArrayList<String>();
            toIds.add(original.getInReplyToScreenName());
            message.setToUsers(toIds);
        }
        message.setDate(original.getCreatedAt());
        return message;
    }
}
