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

package net.frakbot.crowdpulse.extraction.cli;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.entity.Message;
import net.frakbot.crowdpulse.extraction.ExtractorCollection;
import net.frakbot.crowdpulse.extraction.IExtractor;
import net.frakbot.crowdpulse.extraction.exception.InvalidParametersExtractorException;
import net.frakbot.crowdpulse.extraction.twitter.TwitterExtractor;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.io.IOException;

/**
 * @author Francesco Pontillo
 */
public class Main {

    static {
        ExtractorCollection.registerExtractor(new TwitterExtractor());
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Extraction started.");

        ExtractionParameters params = new ExtractionParameters();
        new JCommander(params, args);
        System.out.println("Parameters read.");
        IExtractor extractor = ExtractorCollection.getExtractorImplByParams(params);

        Observable<Message> messages = extractor.getMessages(params);
        messages
                .subscribe(new MessageObserver());

        System.in.read();
    }

    public static class MessageObserver implements Observer<Message> {

        @Override public void onCompleted() {
            System.out.println("Message stream ended.");
        }

        @Override public void onError(Throwable e) {
            System.err.println("Some error occurred.");
            System.err.println(e);
        }

        @Override public void onNext(Message message) {
            System.out.println(message.getText());
        }
    }
}
