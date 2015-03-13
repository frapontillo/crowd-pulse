package it.alessandronatilla.preprocessing.lemmatizer.en;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class LemmatizerEN {

    public static String lemmatizer(String token, String postag) {
        return new StanfordLemmatizer().lemmatize(token, postag);
    }
}
