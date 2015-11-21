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

package net.frakbot.crowdpulse.common.util.rx;

import org.apache.logging.log4j.Logger;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public class StreamSubscriber extends Subscriber<Object> {

    private SubscriptionGroupLatch allSubscriptions;
    private Logger logger;

    public StreamSubscriber(SubscriptionGroupLatch allSubscriptions, Logger logger) {
        this.allSubscriptions = allSubscriptions;
        this.logger = logger;
    }

    @Override
    public void onCompleted() {
        logger.debug("EXECUTION: COMPLETED");
        allSubscriptions.countDown();
    }

    @Override
    public void onError(Throwable e) {
        logger.error("EXECUTION: ERRORED", e);
        allSubscriptions.countDown();
    }

    @Override
    public void onNext(Object o) {
        logger.debug(o.toString());
    }
}
