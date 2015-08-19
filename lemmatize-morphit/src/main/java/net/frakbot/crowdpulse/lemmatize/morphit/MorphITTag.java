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

package net.frakbot.crowdpulse.lemmatize.morphit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Tag (or feature) as handled by Morph-IT.
 * Every tag has:
 * <p>
 * <ul>
 * <li>a main feature (NOUN, ADJ, etc.)</li>
 * <li>zero or more derivational features, separated from the main feature with a dash</li>
 * <li>zero or more inflectional features, separated from the derivational ones with a colon ":"
 * and with each other by a plus sign "+"</li>
 * </ul>
 *
 * @author Francesco Pontillo
 * @see <a href="http://sslmitdev-online.sslmit.unibo.it/linguistics/downloads/readme-morph-it.txt">the official doc</a>
 */
public class MorphITTag {
    private static final Pattern componentsDivider = Pattern.compile(":");
    private static final Pattern derivationalDivider = Pattern.compile("\\-");
    private static final Pattern inflectionalDivider = Pattern.compile("\\+");

    private String originalText;
    private String feature;
    private Set<String> derivationalFeatures;
    private Set<String> inflectionalFeatures;

    public MorphITTag(String text) {
        originalText = text.trim();
        // split the input text (e.g. "VER:part+pres+p+m")
        String[] components = componentsDivider.split(originalText);

        String first = components[0];
        // split the first component, the first element is the main feature, the remaining ones are derivational feats
        String[] firstComponents = derivationalDivider.split(first);
        feature = firstComponents[0];
        derivationalFeatures = new HashSet<>(Arrays.asList(firstComponents).subList(1, firstComponents.length));

        String[] secondComponents = new String[]{};
        if (components.length == 2) {
            String second = components[1];
            // split the second component, all tokens are inflectional features
            secondComponents = inflectionalDivider.split(second);
        }
        inflectionalFeatures = new HashSet<>(Arrays.asList(secondComponents));
    }

    public String getFeature() {
        return feature;
    }

    public Set<String> getInflectionalFeatures() {
        return inflectionalFeatures;
    }

    public Set<String> getDerivationalFeatures() {
        return derivationalFeatures;
    }

    /**
     * Check if the current element is more specific than or equal to another {@link MorphITTag}.
     * <p>
     * For example, "NOUN-M" is more specific than "NOUN", therefore this method would return true.
     * This method will also return true if the features are identical (a {@link MorphITTag} is always child to itself).
     *
     * @param anotherTag The {@link MorphITTag} to check specificity against.
     * @return true if the current {@link MorphITTag} is more specific or equal to the input {@link MorphITTag}.
     */
    public boolean isChildOf(MorphITTag anotherTag) {
        // features must be the same
        // all of the other tag's derivation features must be contained in the current tag
        // all of the other tag's inflectional features must be contained in the current tag
        return (this.feature.equals(anotherTag.feature)
                && this.derivationalFeatures.containsAll(anotherTag.derivationalFeatures)
                && this.inflectionalFeatures.containsAll(anotherTag.inflectionalFeatures));
    }

    public boolean isChildOf(String anotherTag) {
        return this.isChildOf(MorphITTag.from(anotherTag));
    }

    @Override public String toString() {
        return originalText;
    }

    public static MorphITTag from(String text) {
        return new MorphITTag(text);
    }

    /**
     * Check if the first {@link MorphITTag} is equal to or more specific than the second one.
     *
     * @param child  The {@link MorphITTag} that may be more specific
     * @param parent The candidate parent {@link MorphITTag}
     * @return true if child is equal to or more specific than parent
     * @see {@link MorphITTag#isChildOf(MorphITTag)}
     */
    public static boolean isTagChildOfTag(String child, String parent) {
        return MorphITTag.from(child).isChildOf(parent);
    }
}
