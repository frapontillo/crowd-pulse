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

import com.restfb.*;
import com.restfb.types.Comment;
import com.restfb.types.Post;
import net.frakbot.crowdpulse.entity.Message;
import net.frakbot.crowdpulse.extraction.cli.ExtractionParameters;
import net.frakbot.crowdpulse.extraction.util.Checker;
import net.frakbot.crowdpulse.extraction.util.DateUtil;
import net.frakbot.crowdpulse.extraction.util.StringUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import twitter4j.GeoLocation;
import twitter4j.QueryResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class FacebookExtractorRunner {

    private static FacebookClient facebook;
    private static final int POSTS_PER_PAGE = 200;

    public Observable<Message> getMessages(final ExtractionParameters parameters) {

        Subscriber<Message> finalSubscriber = new Subscriber<Message>() {
            @Override public void onCompleted() {
                // TODO: cleanup the facebook instance
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Message message) {
            }
        };

        // initialize the facebook instance
        try {
            getFacebookInstance();
        } catch (IOException e) {
            e.printStackTrace();
            finalSubscriber.onError(e);
        }

        // create the old messages Observable
        Observable<Message> oldMessages = Observable.create(new Observable.OnSubscribe<Message>() {
            @Override public void call(Subscriber<? super Message> subscriber) {
                System.out.println("Started searching.");
                // fetch old messages
                getOldMessages(parameters, subscriber);
            }
        });
        // filter the old messages to properly match the extraction parameters
        oldMessages = oldMessages
                .filter(Checker.checkFromUser(parameters))
                .filter(Checker.checkToUser(parameters))
                .filter(Checker.checkReferencedUsers(parameters))
                .filter(Checker.checkQuery(parameters));

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
        messages.subscribe(finalSubscriber);
        // from this moment on, start fetching posts
        messages.connect();

        return messages;
    }

    public FacebookClient getFacebookInstance() throws IOException {
        if (facebook == null) {
            facebook = new DefaultFacebookClient();
            InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream("facebook.properties");
            Properties properties = new Properties();
            properties.load(propertiesStream);
            FacebookClient.AccessToken accessToken = facebook.obtainAppAccessToken(
                    properties.getProperty("oauth.consumerKey"), properties.getProperty("oauth.consumerSecret"));
            facebook = new DefaultFacebookClient(accessToken.getAccessToken(), properties.getProperty("oauth.consumerSecret"));
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

    /**
     * Download all old messages (maximum 7-10 days past) that match the {@link net.frakbot.crowdpulse.extraction.cli
     * .ExtractionParameters}.
     *
     * @param parameters The {@link net.frakbot.crowdpulse.extraction.cli.ExtractionParameters} with all extraction
     *                   settings.
     * @param subscriber The {@link rx.Subscriber} that will be notified of new tweets, errors and completion.
     */
    private void getOldMessages(ExtractionParameters parameters, Subscriber<? super Message> subscriber) {
        try {
            FacebookClient facebook = getFacebookInstance();
            List<Parameter> query = buildQuery(parameters);
            String endpoint = getEndpoint(parameters);
            FacebookMessageConverter postConverter = new FacebookMessageConverter();
            FacebookCommentConverter commentConverter = new FacebookCommentConverter();
            // fetch first page of data
            Connection<Post> posts = facebook.fetchConnection(endpoint, Post.class, query.toArray(new Parameter[query.size()]));
            Queue<String> nextPages = new LinkedBlockingQueue<String>();
            // continue fetching new pages until there are some
            do {
                // extract the first posts
                List<Post> postList = posts.getData();
                List<Message> messageList = postConverter.fromExtractor(postList);

                // for each post, add all comments
                for (Post post : postList) {
                    // TODO: this will only get the first 25 comments, RestFB currently has this limitation (https://github.com/revetkn/restfb/issues/17)
                    if (post.getComments() != null) {
                        messageList.addAll(commentConverter.fromExtractor(post.getComments().getData()));
                    }
                }

                // notify the subscriber of new posts
                for (Message message : messageList) {
                    subscriber.onNext(message);
                }

                posts = facebook.fetchConnectionPage(posts.getNextPageUrl(), Post.class);
            } while (posts.hasNext());
            // at this point, there is no other available page: we have finished
            subscriber.onCompleted();
        } catch (IOException e) {
            subscriber.onError(e);
        }
    }

    /**
     * Get the proper endpoint to apply the filter parameters to and start searching from.
     *
     * @param parameters The {@link net.frakbot.crowdpulse.extraction.cli.ExtractionParameters} as specified by the user.
     * @return A {@link java.lang.String} representing the endpoint to query.
     */
    private String getEndpoint(ExtractionParameters parameters) {
        // if we have the recipient user, fetch the user's feed (all posts)
        if (!StringUtil.isNullOrEmpty(parameters.getToUser())) {
            return parameters.getToUser() + "/feed";
        }
        // if we only have the author user, get only the posts by that user
        return parameters.getFromUser() + "/posts";
    }

    /**
     * Creates a {@link twitter4j.Query} object from some {@link net.frakbot.crowdpulse.extraction.cli
     * .ExtractionParameters}.
     *
     * @param parameters The source-independent search parameters.
     * @return A {@link twitter4j.Query} Twitter object.
     */
    private List<Parameter> buildQuery(ExtractionParameters parameters) {
        List<Parameter> query = new ArrayList<Parameter>();

        query.add(Parameter.with("limit", POSTS_PER_PAGE));
        query.add(Parameter.with("fields", "from,to,message,comments"));
        if (parameters.getSince() != null) {
            query.add(Parameter.with("since", DateUtil.getTimestamp(parameters.getSince())));
        }
        if (parameters.getUntil() != null) {
            query.add(Parameter.with("until", DateUtil.getTimestamp(parameters.getUntil())));
        }

        return query;
    }

}
