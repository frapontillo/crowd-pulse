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

package net.frakbot.crowdpulse.fixgeomessage;

import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class MessageGeoFixer {
    private final MessageRepository messageRepository = new MessageRepository();
    private final ProfileRepository profileRepository = new ProfileRepository();

    public ConnectableObservable<Message> getGeoConsolidatedMessages(final MessageGeoFixParameters parameters) {

        Observable<Message> messages = Observable.create(new Observable.OnSubscribe<Message>() {
            @Override public void call(Subscriber<? super Message> subscriber) {
                // read all of the candidates
                final List<Message> subsceptibleMessages = messageRepository.getGeoConsolidationCandidates(
                        parameters.getFrom(), parameters.getTo());

                for (Message message : subsceptibleMessages) {
                    Profile user = profileRepository.getByUsername(message.getFromUser());
                    // edit and notify the profile only if lat-lng coordinates were found
                    if (user != null && user.getLatitude() != null && user.getLongitude() != null) {
                        message.setLatitude(user.getLatitude());
                        message.setLongitude(user.getLongitude());
                        subscriber.onNext(message);
                    }
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
