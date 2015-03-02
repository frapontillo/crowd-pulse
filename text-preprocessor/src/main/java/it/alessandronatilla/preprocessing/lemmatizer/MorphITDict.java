package it.alessandronatilla.preprocessing.lemmatizer;

import it.alessandronatilla.preprocessing.lemmatizer.exceptions.EmptyEntryException;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class MorphITDict {

    private Map<Entry, String> morph; //(token, postag) => lemma

    public MorphITDict() {
        morph = new HashMap<Entry, String>();
    }

    public Map<Entry, String> getMorph() {
        return morph;
    }

    public void setMorph(Map<Entry, String> morph) {
        this.morph = morph;
    }

    public void addLemma(Entry entry, String lemma) {
        if (entry == null || entry.posTag == null || entry.posTag.length() == 0
                || entry.token == null || entry.token.length() == 0) throw new EmptyEntryException(entry.toString());

        morph.put(entry, lemma);

    }

    public String getLemma(Entry entry) {
        if (entry == null || entry.posTag == null || entry.posTag.length() == 0
                || entry.token == null || entry.token.length() == 0) throw new EmptyEntryException();

        return morph.get(entry);

    }
}
