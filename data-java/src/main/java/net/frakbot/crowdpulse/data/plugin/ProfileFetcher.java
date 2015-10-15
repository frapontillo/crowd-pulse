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
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

/**
 * An implementation of {@link IPlugin} that, no matter the input stream, waits for its completion and then emits
 * all of the {@link Profile}s stored in the database, eventually completing or erroring.
 * <p>
 * Use this plugin for transforming any stream into a stream containing all previously stored {@link Profile}s.
 *
 * @author Francesco Pontillo
 */
public class ProfileFetcher extends IPlugin<Object, Profile, ProfileFetcher.ProfileFetcherOptions> {
    public final static String PLUGIN_NAME = "profile-fetch";
    private ProfileRepository profileRepository;
    private final Logger logger = CrowdLogger.getLogger(ProfileFetcher.class);

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public ProfileFetcherOptions getNewParameter() {
        return new ProfileFetcherOptions();
    }

    @Override protected Observable.Operator<Profile, Object> getOperator(ProfileFetcherOptions parameters) {
        // use a custom db, if any
        profileRepository = new ProfileRepository(parameters.getDb());

        return subscriber -> new SafeSubscriber<>(new Subscriber<Object>() {
            @Override public void onCompleted() {
                // fetch all messages from the database and subscribe view the new subscriber
                Observable<Profile> dbProfiles = profileRepository.get();
                dbProfiles.subscribe(subscriber);
            }

            @Override public void onError(Throwable e) {
                e.printStackTrace();
                subscriber.onError(e);
            }

            @Override public void onNext(Object o) {
                // do absolutely nothing
            }
        });
    }

    /**
     * Fetching options that include the database name from {@link GenericDbConfig}.
     */
    public class ProfileFetcherOptions extends GenericDbConfig<ProfileFetcherOptions> {
        @Override public ProfileFetcherOptions buildFromJsonElement(JsonElement json) {
            return PluginConfigHelper.buildFromJson(json, ProfileFetcherOptions.class);
        }
    }

}
