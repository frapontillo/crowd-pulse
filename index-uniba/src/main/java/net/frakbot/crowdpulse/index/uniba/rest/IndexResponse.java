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

package net.frakbot.crowdpulse.index.uniba.rest;

import net.frakbot.crowdpulse.common.util.StringUtil;

import java.util.List;

/**
 * Models the outcome of an operation performed by the Uniba indexing service.
 *
 * @author Francesco Pontillo
 */
public class IndexResponse {
    private String OK;
    private String error;
    private List<IndexDocument> succeeded;
    private List<IndexDocument> unsucceeded;

    public String getOK() {
        return OK;
    }

    public void setOK(String OK) {
        this.OK = OK;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<IndexDocument> getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(List<IndexDocument> succeeded) {
        this.succeeded = succeeded;
    }

    public List<IndexDocument> getUnsucceeded() {
        return unsucceeded;
    }

    public void setUnsucceeded(List<IndexDocument> unsucceeded) {
        this.unsucceeded = unsucceeded;
    }

    /**
     * Check if the response contains an error.
     *
     * @return {@code true} if the response has errored, {@code false} otherwise.
     */
    public boolean hasErrored() {
        return !StringUtil.isNullOrEmpty(error);
    }
}
