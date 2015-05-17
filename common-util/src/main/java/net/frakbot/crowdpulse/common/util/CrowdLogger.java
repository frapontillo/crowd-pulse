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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * @author Francesco Pontillo
 */
public class CrowdLogger {

    public static org.apache.logging.log4j.Logger getLogger(Object context) {
        return getLogger(context.getClass().getPackage().getName());
    }

    public static org.apache.logging.log4j.Logger getLogger(Class clazz) {
        return getLogger(clazz.getPackage().getName());
    }

    public static org.apache.logging.log4j.Logger getLogger(String loggerName) {
        System.setProperty("log4j.configurationFile", "log4j2.json");
        org.apache.logging.log4j.Logger logger = LogManager.getLogger(loggerName);
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
        loggerConfig.setLevel(Level.INFO);
        ctx.updateLoggers();
        return logger;
    }

}
