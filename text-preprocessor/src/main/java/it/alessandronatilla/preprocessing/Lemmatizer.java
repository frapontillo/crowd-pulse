package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.lemmatizer.LemmatizerITSingleton;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
class Lemmatizer {



    public String lemmatize(String token, String postag){

        return LemmatizerITSingleton.lemmatizer(token, postag);
    }

}
