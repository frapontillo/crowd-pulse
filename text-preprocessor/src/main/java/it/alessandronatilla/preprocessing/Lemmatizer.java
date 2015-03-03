package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.lemmatizer.MongoMorphITLemmatizer;
import it.alessandronatilla.preprocessing.lemmatizer.OldMorphITLemmatizer;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
class Lemmatizer {



    public String lemmatize(String token, String postag){


        return new MongoMorphITLemmatizer().lemmatize(postag, token);
    }

}
