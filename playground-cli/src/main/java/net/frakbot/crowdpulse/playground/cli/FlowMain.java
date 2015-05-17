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

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.rx.MessagePrintObserver;
import net.frakbot.crowdpulse.social.extraction.ExtractionParameters;
import net.frakbot.crowdpulse.social.extraction.IExtractor;
import org.apache.logging.log4j.Logger;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.util.GregorianCalendar;

/**
 * Playground to test stream composability.
 *
 * @author Francesco Pontillo
 */
public class FlowMain {
    private final static Logger logger = CrowdLogger.getLogger(FlowMain.class);

    public static void main(String args[]) throws ClassNotFoundException {
        new FlowMain().run(args);
    }

    public void run(String args[]) throws ClassNotFoundException {
        IExtractor extractor = PluginProvider.getPlugin("twitter");

        ConnectableObservable<Message> stream = extractor.getMessages(getExtractionParams());

        SubscriptionGroupLatch allSubscriptions = new SubscriptionGroupLatch(1);
        Subscription subscription = stream.subscribe(new MessagePrintObserver(allSubscriptions));
        allSubscriptions.setSubscriptions(subscription);

        stream.connect();
        allSubscriptions.waitAllUnsubscribed();
        logger.info("Done.");
    }

    private ExtractionParameters getExtractionParams() {
        ExtractionParameters extractionParameters = new ExtractionParameters();
        extractionParameters.setFromUser("frapontillo");
        extractionParameters.setSince(new GregorianCalendar(2015, 4, 10).getTime());
        extractionParameters.setUntil(new GregorianCalendar().getTime());
        return extractionParameters;
    }
}
