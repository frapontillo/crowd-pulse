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

import java.util.HashMap;

/**
 * Step contains all the information related to the step to perform:
 * <ul>
 * <li>name of the step</li>
 * <li>type of step (where applicable)</li>
 * <li>a {@link HashMap} of generic configurations</li>
 * </ul>
 *
 * @author Francesco Pontillo
 */
public class Step extends Entity {
    private String name;
    private String type;
    private HashMap<String, Object> config;

    /**
     * Get the name of the Step.
     *
     * @return The name of the Step.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Step.
     *
     * @param name A new name for the Step.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the type of the Step.
     *
     * @return The type of the Step.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type of the Step.
     *
     * @param type The type of the Step.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the configuration {@link HashMap} for the Step.
     *
     * @return The configuration of the Step.
     */
    public HashMap<String, Object> getConfig() {
        return config;
    }

    /**
     * Set a configuration for the Step as a {@link HashMap}.
     *
     * @param config The new configuration for the Step.
     */
    public void setConfig(HashMap<String, Object> config) {
        this.config = config;
    }
}
