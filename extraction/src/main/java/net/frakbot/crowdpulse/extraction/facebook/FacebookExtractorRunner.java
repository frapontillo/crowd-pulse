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

package net.frakbot.crowdpulse.extraction.facebook;

import facebook4j.*;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.extraction.cli.ExtractionParameters;
import net.frakbot.crowdpulse.extraction.util.Checker;
import net.frakbot.crowdpulse.extraction.util.StringUtil;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class FacebookExtractorRunner {
    private static Facebook facebook;

    private static final int POSTS_PER_PAGE = 50;
    private static final int POSTS_POLLING_MINUTES = 1;

    public Observable<Message> getMessages(final ExtractionParameters parameters) {

        // initialize the facebook instances
        try {
            getFacebookInstance();
        } catch (FacebookException e) {
            e.printStackTrace();
        }

        // create the old messages Observable
        Observable<Message> oldMessages = Observable.create(new Observable.OnSubscribe<Message>() {
            @Override public void call(Subscriber<? super Message> subscriber) {
                System.out.println("Started searching.");
                getOldMessages(parameters, subscriber);
            }
        });

        // create the new messages (streamed) Observable
        Observable<Message> newMessages = Observable.create(new Observable.OnSubscribe<Message>() {
            @Override public void call(final Subscriber<? super Message> subscriber) {
                Observable
                        .timer(POSTS_POLLING_MINUTES, POSTS_POLLING_MINUTES, TimeUnit.MINUTES)
                        .subscribe(new Subscriber<Long>() {
                            Date date;
                            @Override public void onStart() {
                                super.onStart();
                                date = new Date();
                                System.out.println("Started streaming.");
                            }

                            @Override public void onCompleted() { /* never completes by design */ }

                            @Override public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override public void onNext(Long aLong) {
                                System.out.println(String.format("Polling attempt number %d.", aLong));
                                Date newDate = new Date();
                                parameters.setSince(date);
                                parameters.setUntil(newDate);
                                date = newDate;
                                getOldMessages(parameters, subscriber, false);
                            }
                        });
            }
        });
        // continue producing elements until the target date is reached
        newMessages = newMessages
                .takeUntil(timeToWait(parameters));

        // both observables should execute the subscribe function in separate threads
        oldMessages = oldMessages.subscribeOn(Schedulers.io());
        newMessages = newMessages.subscribeOn(Schedulers.io());

        // the resulting Observable should be a union of both old and new messages
        // make it as a ConnectableObservable so that multiple subscribers can subscribe to it
        Observable<Message> mergedAndFiltered = Observable
                .merge(oldMessages, newMessages)
                .filter(Checker.checkNonNullMessage())
                .filter(Checker.checkFromUser(parameters))
                .filter(Checker.checkToUser(parameters))
                .filter(Checker.checkReferencedUsers(parameters))
                .filter(Checker.checkQuery(parameters));
        mergedAndFiltered = mergedAndFiltered.subscribeOn(Schedulers.io());

        ConnectableObservable<Message> messages = mergedAndFiltered.publish();

        // subscribe to the merged messages in order to attempt a shutdown of the Facebook instance
        messages.subscribe(new Subscriber<Message>() {
            @Override public void onCompleted() {
                // cleanup Facebook
                facebook.shutdown();
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Message message) {
            }
        });
        // from this moment on, start fetching messages
        messages.connect();

        return messages;
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
            System.out.println(String.format("Shutting down the Streaming service in %d seconds.", timeToDeath/1000));
            return Observable.timer(timeToDeath, TimeUnit.MILLISECONDS, Schedulers.io());
        }
        return Observable.never();
    }

    /**
     * Same as {@link net.frakbot.crowdpulse.extraction.facebook.FacebookExtractorRunner#getOldMessages(net.frakbot.crowdpulse.extraction.cli.ExtractionParameters, rx.Subscriber, boolean)}
     * but without ever notifying the completion.
     *
     * @see net.frakbot.crowdpulse.extraction.facebook.FacebookExtractorRunner#getOldMessages(net.frakbot.crowdpulse.extraction.cli.ExtractionParameters, rx.Subscriber, boolean)
     */
    private void getOldMessages(ExtractionParameters parameters, Subscriber<? super Message> subscriber) {
        getOldMessages(parameters, subscriber, true);
    }

    /**
     * Download all old messages that match the {@link net.frakbot.crowdpulse.extraction.cli.ExtractionParameters}.
     *
     * @param parameters The {@link net.frakbot.crowdpulse.extraction.cli.ExtractionParameters} with all extraction
     *                   settings.
     * @param subscriber The {@link rx.Subscriber} that will be notified of new tweets, errors and completion.
     * @param complete   Whether to notify the completion of posts to the subscriber.
     */
    private void getOldMessages(ExtractionParameters parameters, Subscriber<? super Message> subscriber, boolean
            complete) {
        try {
            Facebook facebook = getFacebookInstance();

            // the endpoint can be a reference string (such as a user name), not an ID
            String endpoint = handleFeedId(parameters, facebook);

            // build the query, prepare the loop data
            Reading query = buildQuery(parameters);
            FacebookMessageConverter converter = new FacebookMessageConverter(parameters);
            Paging<Post> page = null;

            // query can be null if we reach the end of the search result pages
            // urlsToFetch can be empty if there are no more pages to retrieve
            while (query != null || page != null) {
                // get the messages
                ResponseList<Post> postList;
                // if the query is null, it's the first call
                if (query != null) {
                    postList = facebook.getFeed(endpoint, query);
                    // add the next page URL
                    page = postList.getPaging();
                    query = null;
                } else {
                    // retrieve the next page
                    postList = facebook.fetchNext(page);
                    page = postList.getPaging();
                }
                // notify the subscriber of new tweets
                for (Post post : postList) {
                    // convert the message
                    Message message = converter.fromExtractor(post);
                    // since the Facebook API ignores "since" and "until" while paging, thus sucking big time
                    // we have to manually stop notifying new messages
                    if (!Checker.checkSinceDate(parameters).call(message)) {
                        page = null;
                        break;
                    }
                    subscriber.onNext(message);
                }
            }
            // at this point, there is no other available query: we have finished
            if (complete) {
                subscriber.onCompleted();
            }
        } catch (FacebookException e) {
            e.printStackTrace();
            subscriber.onError(e);
        }
    }

    /**
     * Returns a singleton instance of the {@link facebook4j.Facebook} client.
     *
     * @return A set up and ready {@link facebook4j.Facebook} client.
     * @throws facebook4j.FacebookException if the client could not be built.
     */
    private Facebook getFacebookInstance() throws FacebookException {
        if (facebook == null) {
            facebook = new FacebookFactory().getInstance();
            facebook.getOAuthAppAccessToken();
        }
        return facebook;
    }

    /**
     * Get the proper endpoint ID to search the feed or posts on.
     *
     * @param parameters The {@link net.frakbot.crowdpulse.extraction.cli.ExtractionParameters} as specified by the
     *                   user.
     * @return A {@link java.lang.String} representing the endpoint ID to query.
     * @throws facebook4j.FacebookException if the endpoint could not be retrieved.
     */
    private String handleFeedId(ExtractionParameters parameters, Facebook facebook) throws FacebookException {
        String endpoint;

        // if we have the recipient user, fetch the user's feed (all posts)
        if (!StringUtil.isNullOrEmpty(parameters.getToUser())) {
            endpoint = facebook.getUser(parameters.getToUser()).getId();
            parameters.setToUser(endpoint);
        } else {
            // if we only have the author user, get only the posts by that user
            endpoint = facebook.getUser(parameters.getFromUser()).getId();
            parameters.setFromUser(endpoint);
        }

        return endpoint;
    }

    /**
     * Creates a {@link twitter4j.Query} object from some {@link net.frakbot.crowdpulse.extraction.cli
     * .ExtractionParameters}.
     *
     * @param parameters The source-independent search parameters.
     * @return A {@link twitter4j.Query} Twitter object.
     */
    private Reading buildQuery(ExtractionParameters parameters) {
        Reading query = new Reading();

        query.limit(POSTS_PER_PAGE);
        query.fields("from", "to", "message", "comments", "message_tags");
        if (parameters.getSince() != null) {
            query.since(parameters.getSince());
        }
        if (parameters.getUntil() != null) {
            query.until(parameters.getUntil());
        }

        return query;
    }

}
