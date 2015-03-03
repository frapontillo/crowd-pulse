package it.alessandronatilla.preprocessing.lemmatizer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class MorphITDict implements Serializable {

    private Map<LemmaKey, String> dict; //(token, postag) => lemma

    public MorphITDict() {

        dict = new HashMap<LemmaKey, String>();

    }

    public MorphITDict(Map<LemmaKey, String> dict) {
        this.dict = dict;
    }

    public Map<LemmaKey, String> getDict() {

        return dict;
    }

    public void setDict(Map<LemmaKey, String> dict) {

        this.dict = dict;
    }

}
