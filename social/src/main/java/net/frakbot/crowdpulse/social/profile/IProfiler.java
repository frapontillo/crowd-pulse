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

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Profile;
import rx.Observable;

/**
 * Crowd Pulse plugin interface to retrieve a stream of {@link Profile}s starting from a stream of {@link Message}s.
 *
 * @author Francesco Pontillo
 */
public abstract class IProfiler extends IPlugin<Message, Profile, ProfileParameters> {

    /**
     * Gets a {@link Profile} from the given parameters.
     *
     * @param parameters The input {@link ProfileParameters} containing the information to retrieve the profile.
     * @return A {@link Profile} retrieved from the current implementation.
     */
    public abstract Profile getSingleProfile(ProfileParameters parameters);

    @Override public Observable.Transformer<Message, Profile> transform(ProfileParameters params) {
        return messageObservable -> messageObservable
                .distinct(message -> new ProfileKey(message.getSource(), message.getFromUser()))
                .map(message -> {
                    ProfileParameters parameters = new ProfileParameters();
                    parameters.setSource(getName());
                    parameters.setProfile(message.getFromUser());
                    if (params != null) {
                        parameters.setTags(params.getTags());
                    }
                    return getSingleProfile(parameters);
                });
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
