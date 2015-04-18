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

import com.beust.jcommander.Parameter;
import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;

/**
 * @author Francesco Pontillo
 */
public class MessageTagParameters extends GenericAnalysisParameters {
    @Parameter(names = "-tagger", description = "Tagger implementation to use")
    private String tagger;

    public String getTagger() {
        return tagger;
    }

    public void setTagger(String tagger) {
        this.tagger = tagger;
    }
}
