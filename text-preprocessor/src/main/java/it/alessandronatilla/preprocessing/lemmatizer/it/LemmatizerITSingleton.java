package it.alessandronatilla.preprocessing.lemmatizer.it;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class LemmatizerITSingleton {

    private static final String dict_fname = "/lemmatization/morph-it_048.txt";
    private static final String tagset_fname = "/lemmatization/tanl_morph-it";
    private static ItalianLemmatizer lemmatizer;
    private static boolean init = false;

    private LemmatizerITSingleton() {

        URL dict_URL = getClass().getResource(dict_fname);
        URL tagset_URL = getClass().getResource(tagset_fname);
        Path dict_Path = null;
        Path tagset_Path = null;

        try {
            dict_Path = Paths.get(dict_URL.toURI());
            tagset_Path = Paths.get(tagset_URL.toURI());

            lemmatizer = new ItalianLemmatizer(new File(dict_Path.toString()), new File(tagset_Path.toString()));
            init = true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String lemmatizer(String token, String posTag) {

        if (!init) {
            new LemmatizerITSingleton();
        }
        String lemma = null;

        try {
            lemma = lemmatizer.lemmatize(new String[]{token}, new String[]{posTag})[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lemma;
    }
}
