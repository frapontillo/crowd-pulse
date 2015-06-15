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

package net.frakbot.crowdpulse.social.twitter.extraction;

import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.social.extraction.ExtractionParameters;
import net.frakbot.crowdpulse.social.extraction.IReplyExtractor;
import net.frakbot.crowdpulse.social.twitter.TwitterFactory;
import twitter4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Francesco Pontillo
 */
public class TwitterReplyExtractor extends IReplyExtractor {
    public final static String PLUGIN_NAME = "reply-extractor-twitter";

    @Override public List<Message> getReplies(Message message, ExtractionParameters parameters) {
        List<Message> messages = new ArrayList<>();
        TwitterMessageConverter converter = new TwitterMessageConverter(parameters);
        HashMap<String, Object> map = new HashMap<>();
        map.put(TwitterMessageConverter.DATA_REPLY_TO_COMMENT, message.getoId());
        map.put(TwitterMessageConverter.DATA_REPLY_TO_USER, message.getFromUser());
        map.put(TwitterMessageConverter.DATA_SOURCE, parameters.getSource());
        Query query = buildQuery(message);
        try {
            do {
                Twitter twitter = TwitterFactory.getTwitterInstance();
                QueryResult result = twitter.search(query);
                List<Status> statuses = result.getTweets();
                // filter the statuses by returning only the ones in reply to the current message
                statuses = statuses.stream()
                        .filter(status -> status.getInReplyToStatusId() == Long.parseLong(message.getoId()))
                        .collect(Collectors.toList());
                converter.addFromExtractor(statuses, messages, map);
                query = result.nextQuery();
            } while (query != null);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Build the {@link Query} for the {@link Message} search.
     * The results will consist in every {@link Message} addressed to the author since the {@link Message} was posted.
     * {@link Message}s must then be manually filtered in order to include only the ones in reply to the original
     * {@link Message}.
     *
     * @param message   The {@link Message} to retrieve replies for.
     * @return          A Twitter {@link Query} to fetch a superset of replies to the input {@link Message}.
     */
    private Query buildQuery(Message message) {
        Query query = new Query();
        query.setQuery("to:" + message.getFromUser());
        query.setSinceId(Long.parseLong(message.getoId()));
        return query;
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }
}
