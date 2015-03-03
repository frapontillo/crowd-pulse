package it.alessandronatilla.preprocessing.lemmatizer;

import it.alessandronatilla.preprocessing.lemmatizer.exceptions.EmptyEntryException;
import it.alessandronatilla.preprocessing.utils.TanlMorphTagsetConvertion;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class OldMorphITLemmatizer {

    private static String fname = "/dict_lemma.ser";
    private static MorphITDict dict;

    public OldMorphITLemmatizer() {
        URL resourceURL = OldMorphITLemmatizer.class.getClass().getResource(fname);
        Path resourcePath = null;
        BufferedReader reader = null;
        String input;

        try {
            resourcePath = Paths.get(resourceURL.toURI());
//            reader = new BufferedReader(new FileReader(resourcePath.toString()));

            FileInputStream fis = new FileInputStream(resourcePath.toString());
            ObjectInput ois = new ObjectInputStream(fis);
            dict = (MorphITDict) ois.readObject();
            ois.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    private String getLemma(LemmaKey lemmaKey) {
        if (lemmaKey == null || lemmaKey.getPosTag() == null || lemmaKey.getPosTag().length() == 0
                || lemmaKey.getToken() == null || lemmaKey.getToken().length() == 0)
            throw new EmptyEntryException(lemmaKey.toString());

        return dict.getDict().get(lemmaKey);

    }

    public String lemmatize(String token, String postag) {
        if (postag == null || postag.length() == 0
                || token == null || token.length() == 0)
            throw new EmptyEntryException();

        return dict.getDict().get(new LemmaKey(TanlMorphTagsetConvertion.tanl_to_morph_tagset(postag), token));
    }

}
