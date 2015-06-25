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

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Custom {@link rx.Observable.Transformer} that makes the {@link Observable} it is applied on:
 * <ul>
 * <li>subscribe on the computation thread</li>
 * <li>buffer incoming items on backpressure</li>
 * <li>observe on the I/O thread</li>
 * </ul>
 *
 * @author Francesco Pontillo
 */
public class BackpressureAsyncTransformer<T> implements Observable.Transformer<T, T> {
    @Override public Observable<T> call(Observable<T> rObservable) {
        return rObservable
                // don't block waiting on the work in downstream schedulers before generating and processing more values
                .subscribeOn(Schedulers.computation())
                // when downstream observers can't keep up, buffer the generated values
                .onBackpressureBuffer()
                // observer on an IO thread
                .observeOn(Schedulers.io())
                ;
    }
}
