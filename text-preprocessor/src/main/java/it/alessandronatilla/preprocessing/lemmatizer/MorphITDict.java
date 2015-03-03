package it.alessandronatilla.preprocessing.lemmatizer;

import it.alessandronatilla.preprocessing.lemmatizer.exceptions.EmptyEntryException;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class MorphITDict {

    private Map<LemmaKey, String> dict; //(token, postag) => lemma

    public MorphITDict() {
        dict = new HashMap<LemmaKey, String>();
    }

    public Map<LemmaKey, String> getDict() {
        return dict;
    }

    public void setDict(Map<LemmaKey, String> dict) {
        this.dict = dict;
    }

    public void addLemma(LemmaKey lemmaKey, String lemma) {
        if (lemmaKey == null || lemmaKey.getPosTag() == null || lemmaKey.getPosTag().length() == 0
                || lemmaKey.getToken() == null || lemmaKey.getToken().length() == 0)
            throw new EmptyEntryException(lemmaKey.toString());

        dict.put(lemmaKey, lemma);

    }

    public String getLemma(LemmaKey lemmaKey) {
        if (lemmaKey == null || lemmaKey.getPosTag() == null || lemmaKey.getPosTag().length() == 0
                || lemmaKey.getToken() == null || lemmaKey.getToken().length() == 0)
            throw new EmptyEntryException(lemmaKey.toString());

        return dict.get(lemmaKey);

    }
}
