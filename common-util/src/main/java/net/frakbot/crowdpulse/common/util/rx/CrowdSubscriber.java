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

import rx.Subscriber;
import rx.observers.SafeSubscriber;

/**
 * Generic stream {@link SafeSubscriber} that can be used for Crowd Pulse tasks that simply want to handle
 * the {#link Subscriber#onNext} event.
 * <p>
 * Default actions are:
 * <ul>
 * <li>when completed, notifies the completion downstream</li>
 * <li>in case of an error, it prints the stacktrace and notifies the error</li>
 * <li>for each item needing processing, calls a custom method that has to be implemented</li>
 * </ul>
 *
 * @author Francesco Pontillo
 */
public abstract class CrowdSubscriber<T> extends SafeSubscriber<T> {

    public CrowdSubscriber(Subscriber<? super T> actual) {
        super(
                new Subscriber<T>() {
                    @Override
                    public void onCompleted() {
                        actual.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        actual.onError(e);
                    }

                    @Override
                    public void onNext(T t) {
                        onNext(t);
                    }
                });
    }

    public abstract void onNext(T t);
}

