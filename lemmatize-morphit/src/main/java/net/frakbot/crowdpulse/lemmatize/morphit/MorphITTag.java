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

    /**
     * Initialize the MorphITTag by a text (e.g. "VER:part+pres+p+m") that will be split in two parts on the ":"
     * character:
     * <ul>
     * <li>the first part is split on the "-" char, with the first subcomponent being the feature and the
     * (optional) remaining ones being the so-called "derivational features"</li>
     * <li>the second part is split on the "+" char, with each subcomponent being an "inflectional feature"</li>
     * </ul>
     *
     * @param text The input MorphITTag as a {@link String} that will be parsed.
     */
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

    /**
     * Get the tag main feature (e.g. "PRO" from "PRO-INDEF-F-P").
     *
     * @return The main feature of the tag.
     */
    public String getFeature() {
        return feature;
    }

    /**
     * Get the inflectional features of the tag (e.g. "INDEF", "F", "P" from "PRO-INDEF-F-P").
     *
     * @return The {@link Set} of inflectional features of the tag.
     */
    public Set<String> getInflectionalFeatures() {
        return inflectionalFeatures;
    }

    /**
     * Get the derivational features of the tag (e.g. "part", "pres", "p", "m" from "VER:part+pres+p+m").
     *
     * @return The {@link Set} of derivational features of the tag.
     */
    public Set<String> getDerivationalFeatures() {
        return derivationalFeatures;
    }

    /**
     * Check if the current element is more specific than or equal to another {@link MorphITTag}.
     * <p>
     * For example, "NOUN-M" is more specific than "NOUN", therefore this method would return true.
     * This method will also return true if the features are identical (a {@link MorphITTag} is always child to
     * itself).
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

    /**
     * Check if this tag is a specialization of another tag, by building the other tag first.
     *
     * @param anotherTag The other tag to check.
     * @return {@code true} if {@code anotherTag} is more general that the current one, {@code false} otherwise.
     */
    public boolean isChildOf(String anotherTag) {
        return this.isChildOf(MorphITTag.from(anotherTag));
    }

    /**
     * Returns the original text this tag was built from (e.g. "VER:part+pres+p+m").
     *
     * @return A {@link String} representation for the tag.
     */
    @Override public String toString() {
        return originalText;
    }

    /**
     * Static method to create a new MorphITTag from a generic {@link String}.
     *
     * @param text The tag as {@link String} to build the MorphITTag from.
     * @return The MorphITTag built from the input {@code text}.
     * @see #MorphITTag(String)
     */
    public static MorphITTag from(String text) {
        return new MorphITTag(text);
    }

    /**
     * Check if the first {@link MorphITTag} is equal to or more specific than the second one.
     *
     * @param child  The {@link MorphITTag} that may be more specific
     * @param parent The candidate parent {@link MorphITTag}
     * @return true if child is equal to or more specific than parent
     * @see #isChildOf(MorphITTag)
     */
    public static boolean isTagChildOfTag(String child, String parent) {
        return MorphITTag.from(child).isChildOf(parent);
    }
}
