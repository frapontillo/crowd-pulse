package it.alessandronatilla.preprocessing.lemmatizer.en;

import edu.stanford.nlp.process.Morphology;

/**
 * Author: alexander
 * Project: crowd-pulse
 */


class StanfordLemmatizer {

    private Morphology morphology;

    public StanfordLemmatizer() {
        morphology = new Morphology();
    }

    public String lemmatize(String token, String posTag) {
        return morphology.lemma(token, posTag);
    }
}

