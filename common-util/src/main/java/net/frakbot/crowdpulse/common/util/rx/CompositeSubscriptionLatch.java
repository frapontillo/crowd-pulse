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

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import java.util.concurrent.CountDownLatch;

/**
 * @author Francesco Pontillo
 */
public class CompositeSubscriptionLatch implements Subscription {
    private CompositeSubscription compositeSubscription;
    private CountDownLatch countDownLatch;

    public CompositeSubscriptionLatch(int count) {
        countDownLatch = new CountDownLatch(count);
    }

    public void setSubscriptions(final Subscription... subscriptions) {
        compositeSubscription = new CompositeSubscription(subscriptions);
    }

    @Override public void unsubscribe() {
        compositeSubscription.unsubscribe();
    }

    @Override public boolean isUnsubscribed() {
        return compositeSubscription.isUnsubscribed();
    }

    public boolean hasSubscriptions() {
        return compositeSubscription.hasSubscriptions();
    }

    public void countDown() {
        countDownLatch.countDown();
    }

    public void waitAllUnsubscribed() {
        // the thread can be interrupted while "await"-ing, so we "await" again until the subscription is over
        while (!compositeSubscription.isUnsubscribed()) {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) { }
        }
    }
}
