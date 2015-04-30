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

package net.frakbot.crowdpulse.playground.cli;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.rx.BufferedMessageListObserver;
import net.frakbot.crowdpulse.social.extraction.ExtractionParameters;
import net.frakbot.crowdpulse.social.extraction.IExtractor;
import net.frakbot.crowdpulse.social.spi.ExtractorProvider;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class ExtractionMain {

    public static void main(String[] args) throws IOException {
        ExtractionMain main = new ExtractionMain();
        main.run(args);
    }

    public void run(String[] args) throws IOException {
        ExtractionParameters params = new ExtractionParameters();
        new JCommander(params, args);
        IExtractor extractor = ExtractorProvider.getPluginByName(params.getSource());

        ConnectableObservable<Message> messages = extractor.getMessages(params);
        Observable<List<Message>> bufferedMessages = messages.buffer(10, TimeUnit.SECONDS, 3, Schedulers.io());

        SubscriptionGroupLatch allSubscriptions = new SubscriptionGroupLatch(1);
        Subscription bufferedSubscription = bufferedMessages.subscribe(new BufferedMessageListObserver(allSubscriptions));
        allSubscriptions.setSubscriptions(bufferedSubscription);

        messages.connect();
        allSubscriptions.waitAllUnsubscribed();
        MainHelper.getLogger().info("Done.");
    }
}
