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

package net.frakbot.crowdpulse.lemmatize.morphit.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.frakbot.crowdpulse.lemmatize.morphit.MorphITTag;
import org.junit.Test;

/**
 * @author Francesco Pontillo
 */
public class MorphITTagTest {

    @Test
    public void testSimpleEquality() {
        assertTrue(MorphITTag.isTagChildOfTag("NOUN", "NOUN"));
    }

    @Test
    public void testSpecificityWithDerivational() {
        assertTrue(MorphITTag.isTagChildOfTag("NOUN-M", "NOUN"));
    }

    @Test
    public void testSpecificityWithMultipleDerivational() {
        assertTrue(MorphITTag.isTagChildOfTag("NOUN-M-1", "NOUN"));
    }

    @Test
    public void testSpecificityWithInflectional() {
        assertTrue(MorphITTag.isTagChildOfTag("NOUN:p", "NOUN"));
    }

    @Test
    public void testSpecificityWithMultipleInflectional() {
        assertTrue(MorphITTag.isTagChildOfTag("NOUN:p+s", "NOUN"));
    }

    @Test
    public void testSpecificityWithDerivationalAndInflectional() {
        assertTrue(MorphITTag.isTagChildOfTag("NOUN-M:p", "NOUN"));
    }

    @Test
    public void testSpecificityWithMultipleDerivationalAndMultipleInflectional() {
        assertTrue(MorphITTag.isTagChildOfTag("NOUN-M-1-2:p+s+f", "NOUN"));
    }

    @Test
    public void testSpecificityWithHigherComplexity() {
        assertTrue(MorphITTag.isTagChildOfTag("NOUN-M-1-2:p+s+f", "NOUN-M-2:s+p"));
    }

    @Test
    public void testNonSpecificity() {
        assertFalse(MorphITTag.isTagChildOfTag("NOUN-M-2:s+p", "NOUN-M-1-2:p+s+f"));
    }

}
