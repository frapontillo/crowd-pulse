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

package net.frakbot.crowdpulse.detectlanguage.optimaize;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.detectlanguage.ILanguageDetectorOperator;
import rx.Observable;

import java.io.IOException;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class OptimaizeLanguageDetector extends IPlugin<Message, Void> {
    private final static String LANGUAGEDETECTOR_IMPL = "optimaize";
    private final LanguageDetector languageDetector;
    private final TextObjectFactory textObjectFactory;

    public OptimaizeLanguageDetector() {
        // load all languages:
        List<LanguageProfile> languageProfiles = null;
        try {
            languageProfiles = new LanguageProfileReader().readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // build language detector:
        languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles).build();

        // create a text object factory
        textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
    }

    @Override public String getName() {
        return LANGUAGEDETECTOR_IMPL;
    }

    @Override public Observable.Operator<Message, Message> getOperator() {
        return new ILanguageDetectorOperator() {
            @Override public String getLanguage(Message message) {
                TextObject textObject = textObjectFactory.forText(message.getText());
                Optional<String> lang = languageDetector.detect(textObject);
                if (lang.isPresent() && !lang.get().equals("und")) {
                    return lang.get();
                }
                return null;
            }
        };
    }
}
