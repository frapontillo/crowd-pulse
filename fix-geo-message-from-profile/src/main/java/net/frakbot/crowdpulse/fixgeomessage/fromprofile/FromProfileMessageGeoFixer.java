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

package net.frakbot.crowdpulse.fixgeomessage.fromprofile;

import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.plugin.GenericDbConfig;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import net.frakbot.crowdpulse.fixgeomessage.IMessageGeoFixerOperator;
import rx.Observable;

/**
 * @author Francesco Pontillo
 */
public class FromProfileMessageGeoFixer
        extends IPlugin<Message, Message, FromProfileMessageGeoFixer.FromProfileMessageGeoFixerOptions> {
    public final static String PLUGIN_NAME = "fromprofile";
    private ProfileRepository profileRepository;

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public FromProfileMessageGeoFixerOptions getNewParameter() {
        return new FromProfileMessageGeoFixerOptions();
    }

    @Override public Observable.Operator<Message, Message> getOperator(FromProfileMessageGeoFixerOptions parameters) {
        // use the custom DB name, if any
        profileRepository = new ProfileRepository(parameters.getDb());

        return new IMessageGeoFixerOperator(this) {
            @Override public Double[] getCoordinates(Message message) {
                Profile user = profileRepository.getByUsername(message.getFromUser());
                Double[] coordinates = null;
                if (user != null && user.getLatitude() != null && user.getLongitude() != null) {
                    coordinates = new Double[]{user.getLatitude(), user.getLongitude()};
                }
                return coordinates;
            }
        };
    }

    public class FromProfileMessageGeoFixerOptions extends GenericDbConfig<FromProfileMessageGeoFixerOptions> {
        @Override public FromProfileMessageGeoFixerOptions buildFromJsonElement(JsonElement json) {
            return PluginConfigHelper.buildFromJson(json, FromProfileMessageGeoFixerOptions.class);
        }
    }
}
