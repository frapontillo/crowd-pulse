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

import org.mongodb.morphia.annotations.Reference;

import java.util.Date;
import java.util.List;

/**
 * Holds project-level information:
 * <ul>
 * <li>name of the project</li>
 * <li>list of steps to perform</li>
 * <li>creation user and date</li>
 * </ul>
 *
 * @author Francesco Pontillo
 */
public class Project extends Entity {
    private String name;
    @Reference
    private User creationUser;
    private Date creationDate;

    /**
     * Get the name of the Project.
     *
     * @return The name of the Project.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Project.
     *
     * @param name The name of the Project.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the {@link User} who created the Project.
     *
     * @return The {@link User} who created the Project.
     */
    public User getCreationUser() {
        return creationUser;
    }

    /**
     * Set the {@link User} who created the Project.
     *
     * @param creationUser The {@link User} who created the Project.
     */
    public void setCreationUser(User creationUser) {
        this.creationUser = creationUser;
    }

    /**
     * Get the Project {@link Date} of creation.
     *
     * @return The {@link Date} of creation of the Project.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Set the Project {@link Date} of creation.
     *
     * @param creationDate The {@link Date} of creation of the Project.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}

