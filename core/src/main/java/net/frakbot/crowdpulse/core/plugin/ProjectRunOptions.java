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

package net.frakbot.crowdpulse.core.plugin;

import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;
import net.frakbot.crowdpulse.data.plugin.GenericDbConfig;

/**
 * @author Francesco Pontillo
 */
public class ProjectRunOptions extends GenericDbConfig<ProjectRunOptions> {
    private String projectRunId;
    private String log;

    /**
     * Get the ID of the project run to update while executing the plugin using this configuration.
     *
     * @return The ID of a {@link net.frakbot.crowdpulse.data.entity.ProjectRun}, as a {@link String}.
     */
    public String getProjectRunId() {
        return projectRunId;
    }

    /**
     * Set the ID of the project run to update while executing the plugin using this configuration.
     *
     * @param projectRunId The ID of the {@link net.frakbot.crowdpulse.data.entity.ProjectRun}.
     */
    public void setProjectRunId(String projectRunId) {
        this.projectRunId = projectRunId;
    }

    /**
     * Get the location of the log path for the current run.
     *
     * @return The filesystem path of the log.
     */
    public String getLog() {
        return log;
    }

    /**
     * Set the path for the current run log.
     *
     * @param log The filesystem log path.
     */
    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public ProjectRunOptions buildFromJsonElement(JsonElement json) {
        return PluginConfigHelper.buildFromJson(json, ProjectRunOptions.class);
    }
}