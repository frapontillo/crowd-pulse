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

package net.frakbot.crowdpulse.detectlanguage;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import net.frakbot.crowdpulse.common.util.StringUtil;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class LanguageDetector {
    private final MessageRepository messageRepository = new MessageRepository();

    public ConnectableObservable<Message> getMessagesWithLanguages(final LanguageDetectParameters parameters) {

        //load all languages:
        List<LanguageProfile> languageProfiles = null;
        try {
            languageProfiles = new LanguageProfileReader().readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //build language detector:
        final com.optimaize.langdetect.LanguageDetector languageDetector =
                LanguageDetectorBuilder.create(NgramExtractors.standard())
                        .withProfiles(languageProfiles)
                        .build();

        //create a text object factory
        final TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

        Observable<Message> messages = Observable.create(new Observable.OnSubscribe<Message>() {
            @Override public void call(Subscriber<? super Message> subscriber) {
                // read all of the candidates
                final List<Message> subsceptibleMessages = messageRepository.getLanguageDetectionCandidates(
                        parameters.getFrom(), parameters.getTo());

                for (Message message : subsceptibleMessages) {
                    TextObject textObject = textObjectFactory.forText(message.getText());
                    Optional<String> lang = languageDetector.detect(textObject);
                    if (lang.isPresent() && !lang.get().equals("und")) {
                        message.setLanguage(lang.get());
                    }
                    // edit and notify the message only if the language has been found
                    if (!StringUtil.isNullOrEmpty(message.getLanguage())) {
                        subscriber.onNext(message);
                    }
                }

                subscriber.onCompleted();
            }
        });

        messages = messages.onBackpressureBuffer();
        messages = messages.subscribeOn(Schedulers.io());
        messages = messages.observeOn(Schedulers.io());

        return messages.publish();
    }
}
