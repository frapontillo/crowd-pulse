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

package net.frakbot.crowdpulse.tag.opencalais;

import retrofit.RequestInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Francesco Pontillo
 */
public class OpenCalaisInterceptor implements RequestInterceptor {
    private static final String PROP_API_KEY = "opencalais.key";
    private static String API_KEY;

    static {
        InputStream configInput = RequestInterceptor.class.getClassLoader().getResourceAsStream("opencalais.properties");
        Properties prop = new Properties();

        try {
            prop.load(configInput);
        } catch (IOException noFileException) {
            System.err.println(noFileException);
        }
        API_KEY = prop.getProperty(PROP_API_KEY);
    }

    @Override public void intercept(RequestFacade request) {
        request.addHeader("x-calais-licenseID", API_KEY);
    }
}
