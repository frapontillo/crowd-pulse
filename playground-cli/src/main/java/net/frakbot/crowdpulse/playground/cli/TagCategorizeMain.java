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

import net.frakbot.crowdpulse.categorize.ITagCategorizer;
import net.frakbot.crowdpulse.categorize.wikipedia.WikipediaTagCategorizer;
import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;
import net.frakbot.crowdpulse.common.util.rx.BackpressureAsyncTransformer;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.rx.BufferedTagListObserver;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class TagCategorizeMain {
    private SubscriptionGroupLatch allSubscriptions;
    private ITagCategorizer tagCategorizer;

    public static void main(String[] args) throws IOException {
        TagCategorizeMain main = new TagCategorizeMain();
        main.tagCategorizer = new WikipediaTagCategorizer();
        main.run(args);
    }

    public void run(String[] args) throws IOException {
        GenericAnalysisParameters params = MainHelper.start(args);
        final Observable<Tag> candidates = MainHelper.getTags(params);

        ConnectableObservable<Tag> tags = candidates
                .compose(new BackpressureAsyncTransformer<>())
                .map(tagCategorizer::categorizeTag)
                .publish();
        Observable<List<Tag>> bufferedTags = tags.buffer(10, TimeUnit.SECONDS, 3);

        allSubscriptions = new SubscriptionGroupLatch(2);
        Subscription subscription = tags.subscribe(
                tag -> MainHelper.getLogger().info("READ: \"{}\"", tag.getText()),
                throwable -> allSubscriptions.countDown(),
                allSubscriptions::countDown);
        Subscription bufferedSubscription = bufferedTags.subscribe(new BufferedTagListObserver
                (allSubscriptions));
        allSubscriptions.setSubscriptions(subscription, bufferedSubscription);

        tags.connect();

        allSubscriptions.waitAllUnsubscribed();

        MainHelper.getLogger().info("Done.");
    }
}
