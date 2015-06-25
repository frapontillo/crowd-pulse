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

import java.util.concurrent.CountDownLatch;

/**
 * A {@link Subscription} implementation that allows to make the current thread sleep until some actual
 * {@link Subscription}s have finished.
 * <p>
 * It interally uses a {@link CountDownLatch} and still requires that all {@link rx.Subscriber}s call its {@link
 * #countDown()} method to achieve the wait-resume feature.
 *
 * @author Francesco Pontillo
 */
public class SubscriptionGroupLatch implements Subscription {
    private Subscription[] subscriptions;
    private CountDownLatch countDownLatch;

    /**
     * Initializes the object so that it will wait until the input number of {@link Subscription}s are unsubscribed.
     *
     * @param count Number of {@link Subscription}s whose completion will be waited.
     */
    public SubscriptionGroupLatch(int count) {
        countDownLatch = new CountDownLatch(count);
    }

    /**
     * Set the actual {@link Subscription}s to wait.
     *
     * @param subscriptions Array of {@link Subscription}s to wait.
     */
    public void setSubscriptions(final Subscription... subscriptions) {
        this.subscriptions = subscriptions;
        ;
    }

    /**
     * Unsubscribe from all of the set {@link Subscription}s.
     */
    @Override
    public void unsubscribe() {
        for (Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }
    }

    /**
     * Check if all of the set {@link Subscription}s are unsubscribed.
     *
     * @return {@code true} if every {@link Subscription} is unsubscribed, {@code false} otherwise.
     */
    @Override
    public boolean isUnsubscribed() {
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

    /**
     * Lower of a unit the number of subscriptions to wait for.
     */
    public void countDown() {
        countDownLatch.countDown();
    }

    /**
     * Wait for all of the {@link Subscription}s to complete.
     */
    public void waitAllUnsubscribed() {
        // the thread can be interrupted while "await"-ing, so we "await" again until the subscription is over
        while (!this.isUnsubscribed()) {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {
            }
        }
    }
}
