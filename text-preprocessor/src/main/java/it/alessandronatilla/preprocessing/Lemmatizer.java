package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.lemmatizer.en.LemmatizerEN;
import it.alessandronatilla.preprocessing.lemmatizer.it.LemmatizerITSingleton;
import it.alessandronatilla.preprocessing.model.Language;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
class Lemmatizer {

    private final Language language;

    public Lemmatizer(Language language) {
        this.language = language;
    }

    public String lemmatize(String token, String postag) {
        if (this.language.equals(Language.IT))
            return LemmatizerITSingleton.lemmatizer(token, postag);
        if(this.language.equals(Language.EN))
            return LemmatizerEN.lemmatizer(token, postag);
        return null;
    }

}
