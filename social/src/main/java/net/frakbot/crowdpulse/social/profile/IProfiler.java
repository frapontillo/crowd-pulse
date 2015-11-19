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

package net.frakbot.crowdpulse.social.profile;

import net.frakbot.crowdpulse.common.util.rx.RxUtil;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Profile;
import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

import java.util.List;

/**
 * Crowd Pulse plugin interface to retrieve a stream of {@link Profile}s starting from a stream of {@link Message}s.
 *
 * @author Francesco Pontillo
 */
public abstract class IProfiler extends IPlugin<Message, Profile, ProfileParameters> {

    /**
     * Gets a {@link List} of {@link Profile}s from the given parameters.
     *
     * @param parameters The input {@link ProfileParameters} containing the information to retrieve the profiles.
     * @return A {@link List} of {@link Profile}s retrieved from the current implementation.
     */
    public abstract List<Profile> getProfiles(ProfileParameters parameters) throws ProfilerException;

    @Override public Observable.Transformer<Message, Profile> transform(ProfileParameters params) {
        return messageObservable -> messageObservable
                .map(Message::getFromUser)
                .distinct()
                .buffer(100)
                .lift(new Observable.Operator<List<Profile>, List<String>>() {
                    @Override
                    public Subscriber<? super List<String>> call(Subscriber<? super List<Profile>> subscriber) {
                        return new SafeSubscriber<>(new Subscriber<List<String>>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(List<String> profileNames) {
                                ProfileParameters parameters = new ProfileParameters();
                                parameters.setSource(getName());
                                parameters.setProfiles(profileNames);
                                if (params != null) {
                                    parameters.setTags(params.getTags());
                                }
                                profileNames.forEach(IProfiler.this::reportElementAsStarted);
                                List<Profile> profiles = null;
                                try {
                                    profiles = getProfiles(parameters);
                                } catch (ProfilerException e) {
                                    subscriber.onError(e);
                                }
                                profileNames.forEach(IProfiler.this::reportElementAsEnded);
                                subscriber.onNext(profiles);
                            }
                        });
                    }
                })
                .filter(profile -> (profile != null))
                .compose(RxUtil.flatten())
                .doOnCompleted(this::reportPluginAsCompleted)
                .doOnError((err) -> reportPluginAsErrored());
    }

    @Override protected Observable.Operator<Profile, Message> getOperator(ProfileParameters parameters) {
        // we don't need no operator
        // we don't need no thought control...
        return null;
    }

    @Override public ProfileParameters getNewParameter() {
        return new ProfileParameters();
    }
}
