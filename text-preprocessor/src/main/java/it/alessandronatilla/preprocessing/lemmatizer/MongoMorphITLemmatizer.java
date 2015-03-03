package it.alessandronatilla.preprocessing.lemmatizer;

import it.alessandronatilla.preprocessing.utils.TanlMorphTagsetConvertion;
import net.frakbot.crowdpulse.data.repository.LemmaRepository;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class MongoMorphITLemmatizer {

    public String lemmatize(String posTag, String token) {

        LemmaRepository lr = new LemmaRepository();
        String lemma;
        try {
            lemma = lr.getLemma(TanlMorphTagsetConvertion.tanl_to_morph_tagset(posTag), token).getLemma();
        } catch (NullPointerException e) {
            lemma = null;
        }
        return lemma;
    }
}
