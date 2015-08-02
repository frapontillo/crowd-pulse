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

package net.frakbot.crowdpulse.common.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility methods for configuration files.
 *
 * @author Francesco Pontillo
 */
public class ConfigUtil {

    /**
     * Read a properties file and return the associated {@link Properties} object.
     *
     * @param clazz    The class whose classloader must be able to access the properties file.
     * @param fileName The name of the file.
     * @return The {@link Properties} object containing all of the read properties.
     */
    public static Properties getPropertyFile(Class clazz, String fileName) {
        InputStream configInput = ConfigUtil.class.getClassLoader().getResourceAsStream(fileName);
        Properties prop = new Properties();
        try {
            prop.load(configInput);
            return prop;
        } catch (Exception exception) {
            CrowdLogger.getLogger(clazz)
                    .error(String.format("Error while loading the configuration configuration file %s.", fileName),
                            exception);
            return prop;
        }
    }
}
