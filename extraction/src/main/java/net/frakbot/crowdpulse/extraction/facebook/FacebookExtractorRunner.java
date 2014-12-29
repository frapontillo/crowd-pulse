/*
 * Copyright 2014 Francesco Pontillo
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

package net.frakbot.crowdpulse.extraction.facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
import net.frakbot.crowdpulse.entity.Message;
import net.frakbot.crowdpulse.extraction.cli.ExtractionParameters;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class FacebookExtractorRunner {

    private static FacebookClient facebook;

    public Observable<Message> getMessages(final ExtractionParameters parameters) {

        // initialize the facebook instance
        getFacebookInstance();

        // create the old messages Observable
        Observable<Message> oldMessages = Observable.create(new Observable.OnSubscribe<Message>() {
            @Override public void call(Subscriber<? super Message> subscriber) {
                System.out.println("Started searching.");
                // TODO: fetch old messages
                // getOldMessages(parameters, subscriber);
            }
        });

        // create the new messages (streamed) Observable
        Observable<Message> newMessages = Observable.create(new Observable.OnSubscribe<Message>() {
            @Override public void call(Subscriber<? super Message> subscriber) {
                System.out.println("Started streaming.");
                // TODO: listen to new messages
                // getNewMessages(parameters, subscriber);
            }
        });
        // filter the new messages to properly match the extraction parameters
        newMessages = newMessages
                // .filter(checkFromUser(parameters))
                // .filter(checkToUser(parameters))
                // .filter(checkReferencedUsers(parameters))
                // .filter(checkUntilDate(parameters))
                // continue producing elements until the target date is reached
                .takeUntil(timeToWait(parameters));

        // both observables should execute the subscribe function in separate threads
        oldMessages = oldMessages.subscribeOn(Schedulers.io());
        newMessages = newMessages.subscribeOn(Schedulers.io());

        // the resulting Observable should be a union of both old and new messages
        // make it as a ConnectableObservable so that multiple subscribers can subscribe to it
        ConnectableObservable<Message> messages = Observable.merge(oldMessages, newMessages).publish();

        // subscribe to the merged messages in order to attempt a shutdown of the TwitterStreaming
        messages.subscribe(new Subscriber<Message>() {
            @Override public void onCompleted() {
                // TODO: cleanup the facebook instance
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Message message) {
            }
        });
        // from this moment on, start fetching posts
        messages.connect();

        return messages;
    }

    public FacebookClient getFacebookInstance() {
        if (facebook == null) {
            facebook = new DefaultFacebookClient();
            ResourceBundle rb = ResourceBundle.getBundle("facebook.properties");
            facebook.obtainAppAccessToken(rb.getString("oauth.consumerKey"), rb.getString("oauth.consumerSecret"));
        }
        return facebook;
    }

    /**
     * Get the appropriate {@link rx.Observable} that will either complete after a certain amount of time (depending
     * on the input {@link net.frakbot.crowdpulse.extraction.cli.ExtractionParameters#getUntil()}) or never complete.
     *
     * @param parameters The parameters, as set by the user, that can contain a null or valid "until date".
     * @return {@link rx.Observable} that can complete after some time, or will never complete.
     */
    private Observable<Long> timeToWait(ExtractionParameters parameters) {
        if (parameters.getUntil() != null) {
            long timeToDeath = parameters.getUntil().getTime() - new Date().getTime();
            System.out.println(String.format("Shutting down the Streaming service in %d seconds.", timeToDeath));
            return Observable.timer(timeToDeath, TimeUnit.MILLISECONDS, Schedulers.io());
        }
        return Observable.never();
    }

}
