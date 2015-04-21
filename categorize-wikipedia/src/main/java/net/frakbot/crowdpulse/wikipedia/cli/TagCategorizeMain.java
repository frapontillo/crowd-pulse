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

package net.frakbot.crowdpulse.wikipedia.cli;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.repository.TagRepository;
import net.frakbot.crowdpulse.wikipedia.TagCategorizer;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class TagCategorizeMain {
    private SubscriptionGroupLatch allSubscriptions;
    private final Logger logger = CrowdLogger.getLogger(TagCategorizeMain.class);
    private TagCategorizer categorizer;

    public static void main(String[] args) throws IOException {
        TagCategorizeMain main = new TagCategorizeMain();
        main.categorizer = new TagCategorizer();
        main.run(args);
    }

    public void run(String[] args) throws IOException {
        logger.debug("Tag categorization started.");

        // read parameters
        GenericAnalysisParameters params = new GenericAnalysisParameters();
        new JCommander(params, args);
        logger.debug("Parameters read.");

        ConnectableObservable<Tag> tags = categorizer.categorizeTags(params);
        Observable<List<Tag>> bufferedTags =
                tags.buffer(10, TimeUnit.SECONDS, 3, Schedulers.io());

        allSubscriptions = new SubscriptionGroupLatch(1);
        Subscription bufferedSubscription = bufferedTags.subscribe(new BufferedTagListObserver());
        allSubscriptions.setSubscriptions(bufferedSubscription);

        tags.connect();

        allSubscriptions.waitAllUnsubscribed();

        logger.debug("Done.");
    }

    private class BufferedTagListObserver implements Observer<List<Tag>> {
        private final TagRepository tagRepository;

        public BufferedTagListObserver() {
            tagRepository = new TagRepository();
        }

        @Override public void onCompleted() {
            logger.debug("Buffered Tag stream ended.");
            allSubscriptions.countDown();
        }

        @Override public void onError(Throwable e) {
            logger.error("Buffered Tag Stream errored.");
            e.printStackTrace();
            allSubscriptions.countDown();
        }

        @Override public void onNext(List<Tag> tags) {
            for (Tag tag : tags) {
                logger.info(String.format("%s", tag.getText()));
                tagRepository.insertOrUpdate(tag);
            }
        }
    }
}
