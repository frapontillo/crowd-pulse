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

import java.util.Arrays;
import java.util.List;

/**
 * String utility methods.
 *
 * @author Francesco Pontillo
 */
public class StringUtil {

    /**
     * Check if a {@link String} is {@code null} or empty.
     *
     * @param string The {@link String} to check.
     * @return {@code true} if the string is {@code null} or empty, {@code false} otherwise.
     */
    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.equals(""));
    }

    /**
     * Join a {@link List} of {@link String}s with a separator, optionally skipping {@code null} or empty values.
     *
     * @param list     The {@link List<String>} to join.
     * @param with     The separator {@link String} to use (e.g. ", ").
     * @param skipNull {@code true} if {@code null} or empty strings must be skipped.
     * @return The resulting {@link String} created by joining input strings with the separator.
     */
    public static String join(List<String> list, String with, boolean skipNull) {
        StringBuilder builder = new StringBuilder();
        if (list == null) {
            return "";
        }
        list.stream().filter(el -> !(skipNull && isNullOrEmpty(el))).forEach(el -> {
            builder.append(el).append(with);
        });
        if (builder.length() - with.length() > 0) {
            builder.replace(builder.length() - with.length(), builder.length() - 1 + with.length(), "");
        }
        return builder.toString();
    }

    /**
     * Join a {@link List} of {@link String}s with a separator, without skipping {@code null} or empty values.
     *
     * @param list The {@link List<String>} to join.
     * @param with The separator {@link String} to use (e.g. ", ").
     * @return The resulting {@link String} created by joining input strings with the separator.
     * @see StringUtil#join(List, String, boolean)
     */
    public static String join(List<String> list, String with) {
        return join(list, with, false);
    }

    /**
     * Join an array of params {@link String}s with a separator, optionally skipping {@code null} or empty values.
     *
     * @param with     The separator {@link String} to use (e.g. ", ").
     * @param skipNull {@code true} if {@code null} or empty strings must be skipped.
     * @param params   The array of {@link String}s to join.
     * @return The resulting {@link String} created by joining input strings with the separator.
     * @see StringUtil#join(List, String, boolean)
     */
    public static String join(String with, boolean skipNull, String... params) {
        return join(Arrays.asList(params), with, skipNull);
    }

    /**
     * Join an array of params {@link String}s with a separator, without skipping {@code null} or empty values.
     *
     * @param with   The separator {@link String} to use (e.g. ", ").
     * @param params The array of {@link String}s to join.
     * @return The resulting {@link String} created by joining input strings with the separator.
     * @see StringUtil#join(String, boolean, String...)
     */
    public static String join(String with, String... params) {
        return join(with, false, params);
    }

    /**
     * Remove whitespace characters at the beginning of a {@link String}.
     *
     * @param s The {@link String} to cleanse.
     * @return The resulting {@link String} without whitespace characters at the beginning.
     */
    public static String leftTrim(String s) {
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return s.substring(i);
    }

    /**
     * Remove whitespace characters at the end of a {@link String}.
     *
     * @param s The {@link String} to cleanse.
     * @return The resulting {@link String} without whitespace characters at the end.
     */
    public static String rightTrim(String s) {
        int i = s.length() - 1;
        while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
            i--;
        }
        return s.substring(0, i + 1);
    }

    /**
     * Trim a {@link String} at a given character count (or less if the length is less than the input count),
     * then adds the "ellipsis" character.
     *
     * @param s     The {@link String} that will be ellipsized.
     * @param count The maximum number of characters from the input {@link String} that will be preserved.
     * @return A new, ellipsized {@link String}.
     */
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

    /**
     * Check if the source {@link String} contains any of the elements in the input {@link List}.
     *
     * @param source   The {@link String} to check.
     * @param elements A {@link List} of {@link String}s that will be looked for into the source.
     * @return {@code true} if the source contains at least one of the elements, {@code false} otherwise.
     */
    public static boolean containsAnyString(String source, List<String> elements) {
        if (elements == null) {
            return true;
        }
        for (String element : elements) {
            String normalized = element.replaceFirst("^\"", "").replaceFirst("\"$", "");
            if (source.contains(normalized)) {
                return true;
            }
        }
        return false;
    }
}
