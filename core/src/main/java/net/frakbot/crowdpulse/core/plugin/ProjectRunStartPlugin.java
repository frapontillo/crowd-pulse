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

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.DateUtil;
import net.frakbot.crowdpulse.data.entity.ProjectRun;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * @author Francesco Pontillo
 */
public class ProjectRunStartPlugin extends ProjectRunPlugin {
    private static final String PLUGIN_NAME = "project-run-start";
    private final Logger logger = CrowdLogger.getLogger(ProjectRunStartPlugin.class);

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    protected void handleWake(ProjectRun projectRun, ProjectRunOptions parameters, boolean success) {
        projectRun.setDateStart(new Date());
        projectRun.setLog(parameters.getLog());
        projectRun.setPid(getPid());
        logger.info("Project run has started at {}.",
                success ? "completed" : "errored", DateUtil.toISOString(projectRun.getDateEnd()));
    }

    private static Integer getPid() {
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }
}
