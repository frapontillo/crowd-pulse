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

package net.frakbot.crowdpulse.social.facebook.profile;

import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.social.profile.ProfileParameters;
import net.frakbot.crowdpulse.social.profile.Profiler;
import rx.observables.ConnectableObservable;

/**
 * @author Francesco Pontillo
 */
public class FacebookProfiler extends Profiler {
    public static final String PROFILER_NAME = "facebook";
    private static FacebookProfilerRunner runner = null;

    @Override public String getName() {
        return PROFILER_NAME;
    }

    private FacebookProfilerRunner getRunnerInstance() {
        if (runner == null) {
            runner = new FacebookProfilerRunner();
        }
        return runner;
    }

    @Override public ConnectableObservable<Profile> getProfile(ProfileParameters parameters) {
        return getRunnerInstance().getProfile(parameters);
    }
}
