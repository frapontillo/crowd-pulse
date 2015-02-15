package net.frakbot.crowdpulse.extraction.facebook;

import facebook4j.Facebook;
import facebook4j.FacebookException;

/**
 * @author Francesco Pontillo
 */
public class FacebookFactory {
    private static Facebook facebook;

    /**
     * Returns a singleton instance of the {@link facebook4j.Facebook} client.
     *
     * @return A set up and ready {@link facebook4j.Facebook} client.
     * @throws facebook4j.FacebookException if the client could not be built.
     */
    public static Facebook getFacebookInstance() throws FacebookException {
        if (facebook == null) {
            facebook = new facebook4j.FacebookFactory().getInstance();
            facebook.getOAuthAppAccessToken();
        }
        return facebook;
    }
}
