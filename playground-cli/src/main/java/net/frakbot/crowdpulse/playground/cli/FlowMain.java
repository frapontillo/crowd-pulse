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
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.rx.MessagePersister;
import net.frakbot.crowdpulse.data.rx.ProfilePersister;
import net.frakbot.crowdpulse.data.rx.Streamer;
import net.frakbot.crowdpulse.fixgeomessage.fromprofile.FromProfileMessageGeoFixer;
import net.frakbot.crowdpulse.fixgeoprofile.googlemaps.GoogleMapsProfileGeoFixer;
import net.frakbot.crowdpulse.social.extraction.ExtractionParameters;
import net.frakbot.crowdpulse.social.twitter.extraction.TwitterExtractor;
import net.frakbot.crowdpulse.social.twitter.profile.TwitterProfiler;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static net.frakbot.crowdpulse.data.rx.MessagePersister.MessagePersisterOptions;

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
        // get all tasks according to some criteria
        IPlugin<Object, Message, ExtractionParameters> messageExtractor = PluginProvider.getPlugin(TwitterExtractor.PLUGIN_NAME);
        IPlugin<Message, Message, MessagePersisterOptions> messagePersister = PluginProvider.getPlugin(MessagePersister.PLUGIN_NAME);
        IPlugin<Message, Profile, Void> profileExtractor = PluginProvider.getPlugin(TwitterProfiler.PLUGIN_NAME);
        IPlugin<Profile, Profile, Void> profileGLocFixer = PluginProvider.getPlugin(GoogleMapsProfileGeoFixer.PLUGIN_NAME);
        IPlugin<Profile, Profile, Void> profilePersister = PluginProvider.getPlugin(ProfilePersister.PLUGIN_NAME);
        IPlugin<Object, Message, Void> messageSimpleSel = PluginProvider.getPlugin(Streamer.PLUGIN_NAME);
        IPlugin<Message, Message, Void> messageGLocFixer = PluginProvider.getPlugin(FromProfileMessageGeoFixer.PLUGIN_NAME);

        // main stream
        Observable<Message> messageStream;
        // profile stream (temporary)
        Observable<Profile> profileStream;

        // ================================================ PIPELINE ================================================ //

        // extract messages
        messageStream = messageExtractor.process(getExtractionParams());
        messageStream = messagePersister.process(messageStream).cache();

        // right after, extract and process profiles
        profileStream = profileExtractor.process(messageStream);
        profileStream = profileGLocFixer.process(profileStream);
        profileStream = profilePersister.process(profileStream);

        // as soon as profiling is done, keep on processing messages
        messageStream = messageSimpleSel.process(messageStream, profileStream);
        messageStream = messageGLocFixer.process(messageStream);

        // ---------------------------------- TODO: add more processing steps here ---------------------------------- //

        // in the end, save the messages to the database
        messageStream = messagePersister.process(messageStream);

        // ============================================== END PIPELINE ============================================== //

        // observableList will contain the list of the terminal streams
        // terminal streams are the Observables that have to be subscribed on for completion
        List<Observable> observableList = new ArrayList<>();
        // add terminal streams here (e.g. messageStream, profileStream)
        observableList.add(messageStream);
        ConnectableObservable stream = mergeObservables(observableList).publish();

        SubscriptionGroupLatch allSubscriptions = new SubscriptionGroupLatch(1);

        // subscribe to the connectable stream
        Subscription subscription = stream.subscribe(new Subscriber<Object>() {
            @Override public void onCompleted() {
                logger.debug("EXECUTION: COMPLETED");
                allSubscriptions.countDown();
            }

            @Override public void onError(Throwable e) {
                logger.error("EXECUTION: ERRORED");
                allSubscriptions.countDown();
            }

            @Override public void onNext(Object o) {
                logger.debug(o.toString());
            }
        });
        // Subscription subscription2 = messageStream.subscribe(new MessagePrintObserver(allSubscriptions));

        allSubscriptions.setSubscriptions(subscription);
        stream.connect();

        allSubscriptions.waitAllUnsubscribed();
        logger.info("Done.");
    }

    private ExtractionParameters getExtractionParams() {
        ExtractionParameters extractionParameters = new ExtractionParameters();
        extractionParameters.setFromUser("frapontillo");
        extractionParameters.setSince(new GregorianCalendar(2015, 5, 9).getTime());
        extractionParameters.setUntil(new GregorianCalendar().getTime());
        return extractionParameters;
    }

    private Observable mergeObservables(List<Observable> observableList) {
        Observable[] observables = observableList.toArray(new Observable[] {});
        return Observable.merge(observables);
    }
}
