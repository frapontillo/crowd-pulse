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

package net.frakbot.crowdpulse.social.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class StringUtil {
    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.equals(""));
    }

    public static String join(List<String> list, String with, boolean skipNull) {
        StringBuilder builder = new StringBuilder();
        if (list == null) {
            return "";
        }
        for (String el : list) {
            if (!(skipNull && isNullOrEmpty(el))) {
                builder.append(el).append(with);
            }
        }
        if (builder.length() - with.length() > 0) {
            builder.replace(builder.length() - with.length(), builder.length() - 1 + with.length(), "");
        }
        return builder.toString();
    }

    public static String join(List<String> list, String with) {
        return join(list, with, false);
    }

    public static String join(String with, boolean skipNull, String... params) {
        return join(Arrays.asList(params), with, skipNull);
    }

    public static String join(String with, String... params) {
        return join(with, false, params);
    }
}
