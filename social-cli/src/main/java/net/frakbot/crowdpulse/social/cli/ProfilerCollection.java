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

package net.frakbot.crowdpulse.social.cli;

import net.frakbot.crowdpulse.social.profile.ProfileParameters;
import net.frakbot.crowdpulse.social.profile.Profiler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class ProfilerCollection {
    private static List<Profiler> profilers;

    static {
        profilers = new ArrayList<Profiler>();
    }

    public static void registerProfiler(Profiler profiler) {
        profilers.add(profiler);
    }

    public static Profiler getProfilerImplByName(String source) {
        for (Profiler profiler : profilers) {
            if (profiler.getName().equals(source)) {
                return profiler;
            }
        }
        return null;
    }

    public static Profiler getProfilerImplByParams(ProfileParameters parameters) {
        return getProfilerImplByName(parameters.getSource());
    }
}
