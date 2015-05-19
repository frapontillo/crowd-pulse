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
import net.frakbot.crowdpulse.data.entity.Profile;
import rx.observables.ConnectableObservable;

/**
 * @author Francesco Pontillo
 */
public abstract class IProfiler extends IPlugin<Profile, ProfileParameters> {

    /**
     * Starts an asynchronous search loading an {@link rx.Observable} of {@link net.frakbot.crowdpulse.data.entity.Profile}
     * that will be populated as results come in.
     *
     * @param parameters {@link net.frakbot.crowdpulse.social.profile.ProfileParameters} to search for.
     * @return {@link rx.Observable<net.frakbot.crowdpulse.data.entity.Profile>}
     */
    public abstract ConnectableObservable<Profile> getProfile(ProfileParameters parameters);
}
