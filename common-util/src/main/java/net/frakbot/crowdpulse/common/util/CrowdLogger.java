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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Utility logger using Log4J.
 *
 * @author Francesco Pontillo
 */
public class CrowdLogger {

    /**
     * Return a {@link Logger} with a given input name.
     * The logger will read its configuration file from <code>log4j2.json</code>
     * and will force the logging level to DEBUG, no matter what the configuration file says.
     *
     * @param loggerName The name to assign to the {@link Logger}.
     * @return The appropriate {@link Logger} implementation.
     */
    public static Logger getLogger(String loggerName) {
        System.setProperty("log4j.configurationFile", "log4j2.json");
        org.apache.logging.log4j.Logger logger = LogManager.getLogger(loggerName);
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
        loggerConfig.setLevel(Level.DEBUG);
        ctx.updateLoggers();
        return logger;
    }

    /**
     * Return a {@link Logger} from any {@link Class}, using the class package name.
     *
     * @param clazz The {@link Class} to retrieve the logger for.
     * @return The appropriate {@link Logger} implementation.
     * @see CrowdLogger#getLogger(String)
     */
    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getPackage().getName());
    }

    /**
     * Return a {@link Logger} from any object, using the object's class package name.
     *
     * @param context The object to retrieve the logger for.
     * @return The appropriate {@link Logger} implementation.
     * @see CrowdLogger#getLogger(String)
     */
    public static Logger getLogger(Object context) {
        return getLogger(context.getClass());
    }

}
