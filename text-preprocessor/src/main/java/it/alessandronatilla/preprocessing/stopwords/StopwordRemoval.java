package it.alessandronatilla.preprocessing.stopwords;

import it.alessandronatilla.preprocessing.model.Language;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class StopwordRemoval {

    private Language language;

    public StopwordRemoval(Language lang) {
        this.language = lang;
    }

    public StopwordRemoval() {

        language = Language.IT;
    }

    public boolean isStopword(String token) {

        if (language.equals(Language.IT)) {
            if (StopwordsIT.getWords().contains(token)) return true;
        }
        if (language.equals(Language.EN)) {
            if (StopwordsEN.getWords().contains(token)) return true;
        }

        return false;
    }
}
