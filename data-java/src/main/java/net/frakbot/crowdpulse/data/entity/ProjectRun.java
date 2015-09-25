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

package net.frakbot.crowdpulse.data.entity;

import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Holds a specific run of a {@link Project}.
 *
 * @author Francesco Pontillo
 */
public class ProjectRun extends Entity {
    private Date dateStart;
    private Date dateEnd;
    private String log;
    private Integer status;
    private Integer pid;
    private ObjectId project;

    /**
     * Get the {@link Date} this run was started.
     *
     * @return A {@link Date}.
     */
    public Date getDateStart() {
        return dateStart;
    }

    /**
     * Set the {@link Date} this run has started.
     *
     * @param dateStart A {@link Date}.
     */
    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    /**
     * Get the {@link Date} this run ended.
     *
     * @return A {@link Date}.
     */
    public Date getDateEnd() {
        return dateEnd;
    }

    /**
     * Set the {@link Date} this run has ended.
     *
     * @param dateEnd A {@link Date}.
     */
    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    /**
     * Get the log information of the run. According to the specific implementation this may contain
     * the full log as text or the path where the log file can be found.
     *
     * @return A {@link String} representing the log for the run.
     */
    public String getLog() {
        return log;
    }

    /**
     * Set the log for the run. According to the specific implementation this may contain the full
     * log as text or the path where the log file can be found.
     *
     * @param log A {@link String} representing the log for the run.
     */
    public void setLog(String log) {
        this.log = log;
    }

    /**
     * Get the return status of this {@link Project} run.
     * If {@code null}, the run hasn't completed yet.
     *
     * @return The exit code for the specific run.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Set the exit code for this specific {@link Project} run.
     *
     * @param status The exit code for the run.
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Get the process ID on the machine where the process was started.
     *
     * @return A {@link Integer} process ID (PID).
     */
    public Integer getPid() {
        return pid;
    }

    /**
     * Set the process ID that a started process has on the machine where it runs.
     *
     * @param pid The {@link Integer} PID.
     */
    public void setPid(Integer pid) {
        this.pid = pid;
    }

    /**
     * Get the {@link Project} ID this run was started from.
     *
     * @return The {@link ObjectId} of the project.
     */
    public ObjectId getProject() {
        return project;
    }

    /**
     * Set the {@link Project} ID to associate this run with.
     *
     * @param project The {@link ObjectId} of the project.
     */
    public void setProject(ObjectId project) {
        this.project = project;
    }
}
