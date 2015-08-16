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

package net.frakbot.crowdpulse.common.util.spi;

/**
 * Process information.
 *
 * @author Francesco Pontillo
 */
public class ProcessInfo {
    private String name;
    private String logs;

    /**
     * Get the name of the process.
     *
     * @return The name of the process.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the process.
     *
     * @param name The name of the process.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the log path for the process.
     *
     * @return The log path for the process.
     */
    public String getLogs() {
        return logs;
    }

    /**
     * Set the log path for the process.
     *
     * @param logs The log path for the process.
     */
    public void setLogs(String logs) {
        this.logs = logs;
    }
}
