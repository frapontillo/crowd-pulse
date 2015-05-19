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

package net.frakbot.crowdpulse.categorize.test;

import net.frakbot.crowdpulse.categorize.wikipedia.WikipediaTagCategorizer;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Francesco Pontillo
 */
public class WikipediaTagCategorizerTest {

    @Test
    public void testSPI() throws ClassNotFoundException {
        IPlugin<Tag, Void> wikipediaTagCategorizer = PluginProvider.getPlugin(WikipediaTagCategorizer.TAGCATEGORIZER_IMPL);
        assertTrue(wikipediaTagCategorizer.getName().equals(WikipediaTagCategorizer.TAGCATEGORIZER_IMPL));
    }

}
