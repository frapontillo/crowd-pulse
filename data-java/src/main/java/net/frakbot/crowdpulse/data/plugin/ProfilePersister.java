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

package net.frakbot.crowdpulse.data.plugin;

import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import org.apache.logging.log4j.Logger;
import rx.Observable;

/**
 * An implementation of {@link IPlugin} that persists all streamed {@link Profile}s, eventually completing or erroring.
 *
 * @author Francesco Pontillo
 */
public class ProfilePersister extends IPlugin<Profile, Profile, ProfilePersister.ProfilePersisterOptions> {
    public final static String PLUGIN_NAME = "profile-persist";
    private ProfileRepository profileRepository;
    private final Logger logger = CrowdLogger.getLogger(ProfilePersister.class);

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public ProfilePersisterOptions getNewParameter() {
        return new ProfilePersisterOptions();
    }

    @Override protected Observable.Operator<Profile, Profile> getOperator(ProfilePersisterOptions parameters) {
        profileRepository = new ProfileRepository(parameters.getDb());
        return subscriber -> new CrowdSubscriber<Profile>(subscriber) {
            @Override public void onNext(Profile profile) {
                reportElementAsStarted(profile.getId());
                profileRepository.save(profile);
                reportElementAsEnded(profile.getId());
                subscriber.onNext(profile);
            }

            @Override public void onCompleted() {
                reportPluginAsCompleted();
                super.onCompleted();
            }

            @Override public void onError(Throwable e) {
                reportPluginAsErrored();
                super.onError(e);
            }
        };
    }

    /**
     * Options to persist the fetched profiles.
     */
    public class ProfilePersisterOptions extends GenericDbConfig<ProfilePersisterOptions> {
        @Override public ProfilePersisterOptions buildFromJsonElement(JsonElement json) {
            return PluginConfigHelper.buildFromJson(json, ProfilePersisterOptions.class);
        }
    }

}
