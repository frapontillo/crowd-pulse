/*
 * Copyright 2014 Francesco Pontillo
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

package net.frakbot.crowdpulse.entity;

import java.util.HashMap;

/**
 * @author Francesco Pontillo
 */
public class Job {
    private long id;
    private String name;
    private JobType jobType;
    JobExecutionPolicy executionPolicy;
    private HashMap<String, Object> jobConfig;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public JobExecutionPolicy getExecutionPolicy() {
        return executionPolicy;
    }

    public void setExecutionPolicy(JobExecutionPolicy executionPolicy) {
        this.executionPolicy = executionPolicy;
    }

    public HashMap<String, Object> getJobConfig() {
        return jobConfig;
    }

    public void setJobConfig(HashMap<String, Object> jobConfig) {
        this.jobConfig = jobConfig;
    }
}
