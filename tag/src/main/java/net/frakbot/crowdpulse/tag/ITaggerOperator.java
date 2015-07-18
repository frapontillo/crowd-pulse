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

package net.frakbot.crowdpulse.tag;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class ITaggerOperator implements Observable.Operator<Message, Message> {

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = tagMessage(message);
                subscriber.onNext(message);
            }
        };
    }

    /**
     * Starts an asynchronous tagging process loading an {@link List} of
     * {@link net.frakbot.crowdpulse.data.entity.Tag}.
     *
     * @param text {@link String} text to tag
     * @param language {@link String} language of the text to tag (can be discarded by some implementations)
     * @return {@link List <net.frakbot.crowdpulse.data.entity.Tag>}
     */
    public List<Tag> getTags(String text, String language) {
        List<Tag> tags = getTagsImpl(text, language);
        for (Tag tag : tags) {
            tag.setLanguage(language);
        }
        return tags;
    }

    public Message tagMessage(Message message) {
        List<Tag> tags = getTags(message.getText(), message.getLanguage());
        message.addTags(tags);
        return message;
    }

    /**
     * Actual {@link Tag} retrieval implementation.
     *
     * @param text      The text to add {@link Tag}s to.
     * @param language  The language of the text.
     * @return          A {@link List<Tag>}.
     */
    protected abstract List<Tag> getTagsImpl(String text, String language);

}
