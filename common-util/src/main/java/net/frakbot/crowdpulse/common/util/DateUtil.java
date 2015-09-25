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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Date utility methods.
 *
 * @author Francesco Pontillo
 */
public class DateUtil {

    /**
     * Convert a {@link Date} into a {@link String} format.
     *
     * @param date   The {@link Date} to convert.
     * @param format The format to use to convert the string (see {@link SimpleDateFormat}).
     * @return The {@link Date} formatted as a {@link String}.
     */
    public static String toString(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * Get the UNIX epoch time from a {@link Date}.
     *
     * @param date The {@link Date} to convert into Unix Epoch time.
     * @return The desired Unix Epoch representation.
     */
    public static long getUnixEpoch(Date date) {
        return date.getTime() / 1000;
    }

    /**
     * Convert a {@link Date} into a ISO-8601 {@link String} at ZULU time.
     *
     * @param date The {@link Date} to convert.
     * @return A ISO-8601 {@link String}.
     */
    public static String toISOString(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("Z")).toString();
    }
}
