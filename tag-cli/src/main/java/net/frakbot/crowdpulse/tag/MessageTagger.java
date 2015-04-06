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

import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class MessageTagger {
    private final MessageRepository messageRepository = new MessageRepository();

    public ConnectableObservable<Tag> findTagsForMessage(Message message) {
        Observable<Tag> tags = Observable.create(new Observable.OnSubscribe<Tag>() {
            @Override public void call(Subscriber<? super Tag> subscriber) {

                // TODO: retrieve tags for the message

                // for each tag

                    // TODO: edit or insert the tag in the database

                    // notify the new tag
                    // subscriber.onNext(newTag);

                subscriber.onCompleted();
            }
        });

        tags = tags.subscribeOn(Schedulers.io());
        tags = tags.observeOn(Schedulers.io());

        ConnectableObservable connectableTags = tags.publish();
        return connectableTags;
    }

    public ConnectableObservable<Message> tagMessages(final GenericAnalysisParameters parameters) {

        Observable<Message> messages = Observable.create(new Observable.OnSubscribe<Message>() {
            @Override public void call(Subscriber<? super Message> subscriber) {
                // read all of the messages
                final List<Message> allMessages = messageRepository.getBetweenIds(
                        parameters.getFrom(), parameters.getTo());

                for (Message message : allMessages) {

                }

                subscriber.onCompleted();
            }
        });
        messages = messages.onBackpressureBuffer();
        messages = messages.subscribeOn(Schedulers.io());
        messages = messages.observeOn(Schedulers.io());

        ConnectableObservable connectableMessages = messages.publish();
        return connectableMessages;
    }
}
