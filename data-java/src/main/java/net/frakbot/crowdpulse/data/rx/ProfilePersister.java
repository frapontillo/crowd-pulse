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

package net.frakbot.crowdpulse.data.rx;

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import org.apache.logging.log4j.Logger;
import rx.Observable;

/**
 * @author Francesco Pontillo
 */
public class ProfilePersister extends IPlugin<Profile, Profile, Void> {
    public final static String PLUGIN_NAME = "profile-persist";
    private final ProfileRepository profileRepository;
    private final Logger logger = CrowdLogger.getLogger(ProfilePersister.class);

    public ProfilePersister() {
        profileRepository = new ProfileRepository();
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override protected Observable.Operator<Profile, Profile> getOperator(Void parameters) {
        return subscriber -> new CrowdSubscriber<Profile>(subscriber) {
            @Override public void onNext(Profile profile) {
                profileRepository.save(profile);
                subscriber.onNext(profile);
            }
        };
    }
}