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

package net.frakbot.crowdpulse.social.twitter;

import org.apache.logging.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileNotFoundException;

/**
 * @author Francesco Pontillo
 */
public class TwitterFactory {
    private static Twitter twitter;
    private static TwitterStream twitterStream;

    /**
     * Returns a singleton instance of the {@link twitter4j.Twitter} client.
     *
     * @return A set up and ready {@link twitter4j.Twitter} client.
     * @throws twitter4j.TwitterException if the client could not be built.
     */
    public static Twitter getTwitterInstance() throws TwitterException {
        if (twitter == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder()
                    .setApplicationOnlyAuthEnabled(true)
                    .setDebugEnabled(false);
            twitter4j.TwitterFactory tf = new twitter4j.TwitterFactory(cb.build());
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
    public static TwitterStream getTwitterStreamInstance() throws TwitterException {
        if (twitterStream == null) {
            twitterStream = new TwitterStreamFactory().getInstance();
        }
        return twitterStream;
    }

    /**
     * Stops the current thread for the amount of time needed to recover from a "rate limit exceeded"
     * error on the Twitter API side.
     *
     * @param exception The original rate limit exception to retrieve the amount of time to wait from.
     * @param logger    The logger to use to print messages.
     * @return {@code true} when the rate limit should be expired, {@code false} if the exception was not
     * rate limit related.
     * @throws InterruptedException if there's an issue with the current thread sleeping.
     */
    public static boolean waitForTwitterTimeout(TwitterException exception, Logger logger)
            throws InterruptedException {
        if (exception.getCause() != null && exception.getCause().getClass() == FileNotFoundException.class) {
            return true;
        }
        if (exception.getRateLimitStatus() == null) {
            exception.printStackTrace();
            return false;
        }
        int remaining = exception.getRateLimitStatus().getRemaining();
        if (remaining <= 0) {
            int secondsToWait = exception.getRateLimitStatus().getSecondsUntilReset() + 5;
            logger.warn("Encountered Twitter rate limit, waiting for {} seconds...", secondsToWait);
            Thread.sleep(1000 * secondsToWait);
            logger.warn("{} seconds have elapsed, now retrying the Twitter call...", secondsToWait);
            // return true if the exception was rate limit related
            return true;
        }
        // return false otherwise
        return false;
    }
}
