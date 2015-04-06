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

package net.frakbot.crowdpulse.tag;

import dagger.Module;
import dagger.Provides;
import net.frakbot.crowdpulse.tag.cli.MessageTagMain;
import net.frakbot.crowdpulse.tag.tagme.TagMeTagger;
import net.frakbot.crowdpulse.tag.wikipediaminer.WikipediaMinerTagger;
import net.frakbot.crowdpulse.tag.zemanta.ZemantaTagger;

/**
 * @author Francesco Pontillo
 */
@Module(injects = MessageTagMain.class)
public class TaggerModule {

    @Provides(type= Provides.Type.SET) ITagger provideZemantaTagger() {
        return new ZemantaTagger();
    }

    @Provides(type= Provides.Type.SET) ITagger provideWikipediaMinerTagger() {
        return new WikipediaMinerTagger();
    }

    @Provides(type= Provides.Type.SET) ITagger provideTagMeTagger() {
        return new TagMeTagger();
    }

    /*
    @Provides(type= Provides.Type.SET) ITagger provideOpenCalaisTagger() {
        return new OpenCalaisTagger();
    }

    @Provides(type= Provides.Type.SET) ITagger provideBabelfyTagger() {
        return new BabelfyTagger();
    }
    */
}
