/*
 * Copyright 2014 Francesco Pontillo
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

package net.frakbot.crowdpulse.extraction;

import net.frakbot.crowdpulse.extraction.cli.ExtractionParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class ExtractorCollection {
    private static List<IExtractor> extractors;

    static {
        extractors = new ArrayList<IExtractor>();
    }

    public static void registerExtractor(IExtractor extractor) {
        extractors.add(extractor);
    }

    public static IExtractor getExtractorImplByName(String source) {
        for (IExtractor extractor : extractors) {
            if (extractor.getName().equals(source)) {
                return extractor;
            }
        }
        return null;
    }

    public static IExtractor getExtractorImplByParams(ExtractionParameters parameters) {
        return getExtractorImplByName(parameters.getSource());
    }
}
