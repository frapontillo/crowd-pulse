package it.alessandronatilla.preprocessing.lemmatizer.en;

import edu.stanford.nlp.process.Morphology;

/**
 * Author: alexander
 * Project: crowd-pulse
 *
 * @see "https://code.google.com/p/dkpro-core-gpl/source/browse/de.tudarmstadt.ukp.dkpro.core-gpl/trunk/de.tudarmstadt.ukp.dkpro.core.stanfordnlp-gpl/src/main/java/de/tudarmstadt/ukp/dkpro/core/stanfordnlp/StanfordLemmatizer.java?r=153"
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

