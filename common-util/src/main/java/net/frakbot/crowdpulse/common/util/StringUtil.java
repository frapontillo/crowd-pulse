package net.frakbot.crowdpulse.common.util;/*
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

    public static String leftTrim(String s) {
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return s.substring(i);
    }

    public static String rightTrim(String s) {
        int i = s.length()-1;
        while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
            i--;
        }
        return s.substring(0,i+1);
    }

    public static String ellipsize(String s, int count) {
        if (isNullOrEmpty(s)) {
            return s;
        }
        String newString = s.substring(0, Math.min(s.length() - 1, count));
        if (s.length() - 1 > count) {
            newString += "…";
        }
        return newString;
    }
}
