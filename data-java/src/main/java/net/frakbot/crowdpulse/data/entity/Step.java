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
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class Step {
    @Id private ObjectId id;
    private String name;
    Integer notifyEvery;
    boolean waitPrevStep;
    @Reference private List<Job> jobs;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNotifyEvery() {
        return notifyEvery;
    }

    public void setNotifyEvery(Integer notifyEvery) {
        this.notifyEvery = notifyEvery;
    }

    public boolean isWaitPrevStep() {
        return waitPrevStep;
    }

    public void setWaitPrevStep(boolean waitPrevStep) {
        this.waitPrevStep = waitPrevStep;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
