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
public class SubscriptionGroupLatch implements Subscription {
    private Subscription[] subscriptions;
    private CountDownLatch countDownLatch;

    public SubscriptionGroupLatch(int count) {
        countDownLatch = new CountDownLatch(count);
    }

    public void setSubscriptions(final Subscription... subscriptions) {
        this.subscriptions = subscriptions;;
    }

    @Override public void unsubscribe() {
        for (Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }
    }

    @Override public boolean isUnsubscribed() {
        if (subscriptions == null) {
            return true;
        }
        for (Subscription subscription : subscriptions) {
            if (!subscription.isUnsubscribed()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasSubscriptions() {
        return (subscriptions != null && subscriptions.length > 0);
    }

    public void countDown() {
        countDownLatch.countDown();
    }

    public void waitAllUnsubscribed() {
        // the thread can be interrupted while "await"-ing, so we "await" again until the subscription is over
        while (!this.isUnsubscribed()) {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) { }
        }
    }
}
