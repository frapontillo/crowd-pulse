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

package net.frakbot.crowdpulse.data.rx;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 * TODO: is this really necessary?
 */
public class SelectiveMerger extends IPlugin<Observable, Message, Void> {
    public final static String PLUGIN_NAME = "selective-merger";

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public Observable<Message> processSingle(Void params, Observable<Observable> stream) {
        return super.processSingle(params, stream);
    }

    @Override protected Observable.Operator<Message, Observable> getOperator(Void parameters) {
        return new Observable.Operator<Message, Observable>() {
            @Override public Subscriber<? super Observable> call(Subscriber<? super Message> subscriber) {
                return null;
            }
        };
    }
}