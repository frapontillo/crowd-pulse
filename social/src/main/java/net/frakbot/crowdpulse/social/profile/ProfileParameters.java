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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.spi.IPluginConfig;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
@Parameters(separators = "=")
public class ProfileParameters implements IPluginConfig<ProfileParameters> {
    @Parameter(names = "-source", description = "Source for extraction")
    private String source;

    @Parameter(names = "-profile", description = "Profile ID or name to fetch information from")
    private String profile;

    @Parameter(names = "-tags", description = "Tags to add to profiles")
    private List<String> tags;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override public ProfileParameters buildFromJsonElement(JsonElement json) {
        return PluginConfigHelper.buildFromJson(json, ProfileParameters.class);
    }
}
