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

package net.frakbot.crowdpulse.extraction.twitter;

import net.frakbot.crowdpulse.entity.Message;
import net.frakbot.crowdpulse.extraction.cli.ExtractionParameters;
import net.frakbot.crowdpulse.extraction.util.*;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class TwitterExtractorRunner {
    private static Twitter twitter;
    private static TwitterStream twitterStream;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int TWEETS_PER_PAGE = 1;

    public Observable<Message> getMessages(final ExtractionParameters parameters) {

        // initialize the twitter instances
        try {
            getTwitterInstance();
            getTwitterStreamInstance();
        } catch (TwitterException e) {
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
            @Override public void call(Subscriber<? super Message> subscriber) {
                System.out.println("Started streaming.");
                getNewMessages(parameters, subscriber);
            }
        });
        // filter the new messages to properly match the extraction parameters
        newMessages = newMessages
                .filter(checkFromUser(parameters))
                .filter(checkToUser(parameters))
                .filter(checkReferencedUsers(parameters))
                .filter(checkUntilDate(parameters))
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
                try {
                    getTwitterStreamInstance().cleanUp();
                    getTwitterStreamInstance().shutdown();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Message message) {
            }
        });
        // from this moment on, start fetching tweets
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
            Twitter twitter = getTwitterInstance();
            Query query = buildQuery(parameters);
            TwitterMessageConverter converter = new TwitterMessageConverter();
            // query can be null if we reach the end of the search result pages
            while (query != null) {
                // get the tweets and convert them
                QueryResult result = twitter.search(query);
                List<Status> tweetList = result.getTweets();
                List<Message> messageList = converter.fromExtractor(tweetList);
                // notify the subscriber of new tweets
                for (Message message : messageList) {
                    subscriber.onNext(message);
                }
                // get the next page query
                query = result.nextQuery();
            }
            // at this point, there is no other available query: we have finished
            subscriber.onCompleted();
        } catch (TwitterException e) {
            subscriber.onError(e);
        }
    }

    /**
     * Open up a stream to Twitter, listening to new tweets according to the {@link net.frakbot.crowdpulse.extraction
     * .cli.ExtractionParameters}.
     *
     * @param parameters The {@link net.frakbot.crowdpulse.extraction.cli.ExtractionParameters} with all extraction
     *                   settings.
     * @param subscriber The {@link rx.Subscriber} that will be notified of new tweets, errors and completion.
     */
    private void getNewMessages(ExtractionParameters parameters, final Subscriber<? super Message> subscriber) {
        try {
            TwitterStream twitterStream = getTwitterStreamInstance();
            final TwitterMessageConverter converter = new TwitterMessageConverter();
            twitterStream.addListener(new StatusListener() {
                @Override public void onStatus(Status status) {
                    subscriber.onNext(converter.fromExtractor(status));
                }

                @Override public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                    // ignore
                }

                @Override public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                    // ignore
                }

                @Override public void onScrubGeo(long userId, long upToStatusId) {
                    // ignore
                }

                @Override public void onStallWarning(StallWarning warning) {
                    System.err.println(warning);
                }

                @Override public void onException(Exception ex) {
                    subscriber.onError(ex);
                }
            });
            FilterQuery filterQuery = buildFilterQuery(parameters);
            twitterStream.filter(filterQuery);
        } catch (TwitterException e) {
            subscriber.onError(e);
        }
    }

    /**
     * Returns a singleton instance of the {@link twitter4j.Twitter} client.
     *
     * @return A set up and ready {@link twitter4j.Twitter} client.
     * @throws TwitterException if the client could not be built.
     */
    private Twitter getTwitterInstance() throws TwitterException {
        if (twitter == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder()
                    .setApplicationOnlyAuthEnabled(true)
                    .setDebugEnabled(false);
            TwitterFactory tf = new TwitterFactory(cb.build());
            twitter = tf.getInstance();
            twitter.getOAuth2Token();
        }
        return twitter;
    }

    /**
     * Returns a singleton instance of the {@link twitter4j.Twitter} client.
     *
     * @return A set up and ready {@link twitter4j.TwitterStream} client.
     * @throws TwitterException if the client could not be built.
     */
    private TwitterStream getTwitterStreamInstance() throws TwitterException {
        if (twitterStream == null) {
            twitterStream = new TwitterStreamFactory().getInstance();
        }
        return twitterStream;
    }

    /**
     * Creates a {@link twitter4j.Query} object from some {@link net.frakbot.crowdpulse.extraction.cli
     * .ExtractionParameters}.
     *
     * @param parameters The source-independent search parameters.
     * @return A {@link twitter4j.Query} Twitter object.
     */
    private Query buildQuery(ExtractionParameters parameters) {
        Query query = new Query();

        if (parameters.getGeoLocationBox() != null) {
            query.setGeoCode(new GeoLocation(parameters.getGeoLocationBox().getLatitude(), parameters
                    .getGeoLocationBox().getLongitude()), parameters.getGeoLocationBox().getDistance(), Query.Unit.km);
        }
        if (!StringUtil.isNullOrEmpty(parameters.getLanguage())) {
            query.setLang(parameters.getLanguage());
        }
        if (!StringUtil.isNullOrEmpty(parameters.getLocale())) {
            query.setLocale(parameters.getLocale());
        }
        if (parameters.getSince() != null) {
            query.setSince(DateUtil.toString(parameters.getSince(), DATE_FORMAT));
        }
        if (parameters.getUntil() != null) {
            query.setUntil(DateUtil.toString(parameters.getUntil(), DATE_FORMAT));
        }

        // start building the main query text
        StringBuilder queryStringBuilder = new StringBuilder();
        if (!StringUtil.isNullOrEmpty(parameters.getFromUser())) {
            queryStringBuilder.append("from:").append(parameters.getFromUser()).append(" ");
        }
        if (!StringUtil.isNullOrEmpty(parameters.getToUser())) {
            queryStringBuilder.append("to:").append(parameters.getToUser()).append(" ");
        }
        if (parameters.getReferenceUsers() != null && parameters.getReferenceUsers().size() > 0) {
            for (String user : parameters.getReferenceUsers()) {
                if (!StringUtil.isNullOrEmpty(user)) {
                    queryStringBuilder.append("@").append(user).append(" ");
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(parameters.getQuery())) {
            String[] components = parameters.getQuery().split(",");
            for (String component : components) {
                queryStringBuilder.append("\"").append(component).append("\" ");
            }
        }

        query.setQuery(queryStringBuilder.toString());
        query.setResultType(Query.ResultType.recent);
        query.setCount(TWEETS_PER_PAGE);

        return query;
    }

    /**
     * Creates a {@link twitter4j.FilterQuery} object from some {@link net.frakbot.crowdpulse.extraction.cli
     * .ExtractionParameters}.
     *
     * @param parameters The source-independent search parameters.
     * @return A {@link twitter4j.FilterQuery} Twitter object.
     * @throws TwitterException if the query could not be built because of some issues instantiating the regular client.
     */
    private FilterQuery buildFilterQuery(ExtractionParameters parameters) throws TwitterException {
        FilterQuery filterQuery = new FilterQuery();

        // build the location parameters
        if (parameters.getGeoLocationBox() != null) {
            filterQuery.locations(parameters.getGeoLocationBox().getBoundingBox());
        }

        // set the language
        if (!StringUtil.isNullOrEmpty(parameters.getLanguage())) {
            filterQuery.language(new String[]{parameters.getLanguage()});
        }

        // set the users to retrieve tweets from
        List<String> users = new ArrayList<String>();
        if (!StringUtil.isNullOrEmpty(parameters.getFromUser())) {
            users.add(parameters.getFromUser());
        }
        if (!StringUtil.isNullOrEmpty(parameters.getToUser())) {
            users.add(parameters.getToUser());
        }
        if (parameters.getReferenceUsers() != null && parameters.getReferenceUsers().size() > 0) {
            for (String user : parameters.getReferenceUsers()) {
                if (!StringUtil.isNullOrEmpty(user)) {
                    users.add(user);
                }
            }
        }
        // convert user names into user IDs (long) using the Twitter API
        ResponseList<User> twitterUsers = getTwitterInstance().users().lookupUsers(users.toArray(new String[users
                .size()]));
        long[] twitterUserIds = new long[twitterUsers.size()];
        int i = 0;
        for (User user : twitterUsers) {
            twitterUserIds[i] = user.getId();
            i += 1;
        }
        filterQuery.follow(twitterUserIds);

        // set the terms to search for
        if (!StringUtil.isNullOrEmpty(parameters.getQuery())) {
            filterQuery.track(parameters.getQuery().split(","));
        }

        return filterQuery;
    }

    private Func1<Message, Boolean> checkFromUser(final ExtractionParameters parameters) {
        return new Func1<Message, Boolean>() {
            @Override public Boolean call(Message message) {
                return (StringUtil.isNullOrEmpty(parameters.getFromUser()) || parameters.getFromUser().equals(message
                        .getFromUser()));
            }
        };
    }

    private Func1<Message, Boolean> checkToUser(final ExtractionParameters parameters) {
        return new Func1<Message, Boolean>() {
            @Override public Boolean call(Message message) {
                return (StringUtil.isNullOrEmpty(parameters.getToUser()) || parameters.getToUser().equals(message
                        .getToUser()));
            }
        };
    }

    private Func1<Message, Boolean> checkReferencedUsers(final ExtractionParameters parameters) {
        return new Func1<Message, Boolean>() {
            @Override public Boolean call(Message message) {
                // if no referenced users are requested
                if (parameters.getReferenceUsers() == null || parameters.getReferenceUsers().size() <= 0) {
                    return true;
                }
                // if some referenced users are requested, all tweets should contain those
                // BUT we need to exclude all tweets whose recipient user matches a referenced user
                // (referenced users are all users involved in a tweet but the recipient)
                return !parameters.getReferenceUsers().contains(message.getToUser());
            }
        };
    }

    private Func1<Message, Boolean> checkUntilDate(final ExtractionParameters parameters) {
        return new Func1<Message, Boolean>() {
            @Override public Boolean call(Message message) {
                return message.getDate().compareTo(parameters.getUntil()) <= 0;
            }
        };
    }
}
