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

package net.frakbot.crowdpulse.data.plugin;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.VoidConfig;
import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

/**
 * An implementation of {@link IPlugin} that has no effect on input elements, emitting them untouched. Error and
 * completion events are notified as well.
 *
 * @author Francesco Pontillo
 */
public class Streamer extends IPlugin<Object, Object, VoidConfig> {
    public final static String PLUGIN_NAME = "streamer";

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig getNewParameter() {
        return new VoidConfig();
    }

    @Override protected Observable.Operator<Object, Object> getOperator(VoidConfig parameters) {
        return subscriber -> new SafeSubscriber<>(new Subscriber<Object>() {
            @Override public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override public void onNext(Object o) {
                subscriber.onNext(o);
            }
        });
    }
}
