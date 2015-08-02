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

package net.frakbot.crowdpulse.tag.opencalais.rest;

import net.frakbot.crowdpulse.common.util.ConfigUtil;
import retrofit.RequestInterceptor;

import java.util.Properties;

/**
 * @author Francesco Pontillo
 */
public class OpenCalaisInterceptor implements RequestInterceptor {
    private static final String PROP_API_KEY = "opencalais.key";
    private String API_KEY;

    private String getApiKey() {
        if (API_KEY == null) {
            Properties props = ConfigUtil.getPropertyFile(this.getClass(), "opencalais.properties");
            API_KEY = props.getProperty(PROP_API_KEY, "");
        }
        return API_KEY;
    }

    @Override public void intercept(RequestFacade request) {
        request.addHeader("x-ag-access-token", getApiKey());
    }
}
