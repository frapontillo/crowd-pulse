package it.alessandronatilla.preprocessing.lemmatizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pierpaolo
 */
class ItalianLemmatizer {

    private boolean init = false;

    private Map<String, String> dict;

    public ItalianLemmatizer(File dictFile, File posTagConversionFile) throws Exception {
        init(dictFile, posTagConversionFile);
    }

    private void init(File dictFile, File posTagConversionFile) throws Exception {
        Map<String, Set<String>> postagMap = buildMorpht2TanlPosTagMap(posTagConversionFile);
        dict = loadDict(dictFile, postagMap);
        init = true;
    }

    public String[] lemmatize(String[] tokens, String[] posTag) throws Exception {

        if (!init) {
            throw new Exception("Pipeline is not inizialized");
        }

        if (tokens.length != posTag.length) {
            throw new IllegalArgumentException("Tokens and pos-tags with different size");
        }

        String[] lemmas = new String[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            String lemma = dict.get(tokens[i] + "_" + posTag[i]);

            if (lemma == null) {
                String failsafeTagTanl = getFailsafeTagTanl(posTag[i]);

                if (failsafeTagTanl != null) {
                    lemma = dict.get(tokens[i] + "_#" + failsafeTagTanl + "#");

                } else {
                    lemma = dict.get(tokens[i] + "_#!#");

                }
                if (lemma == null) {
                    lemma = tokens[i].toLowerCase();
                }
            }
            lemmas[i] = lemma;
        }
        return lemmas;
    }

    private String getFailsafeTagTanl(String tag) {
        if (tag.startsWith("S")) {
            return "N";
        } else if (tag.startsWith("V")) {
            return "V";
        } else if (tag.startsWith("B")) {
            return "R";
        } else if (tag.startsWith("A") || tag.startsWith("NO")) {
            return "A";
        }
        return null;
    }

    private String getFailsafeTagMorpthIt(String tag) {
        if (tag.contains("NOUN")) {
            return "N";
        } else if (tag.contains("VER") || tag.contains("AUX") || tag.contains("MOD")) {
            return "V";
        } else if (tag.contains("ADV")) {
            return "R";
        } else if (tag.contains("ADJ")) {
            return "A";
        }
        return null;
    }

    private Map<String, String> loadDict(File dictFile, Map<String, Set<String>> posTagMap) throws IOException {
        Map<String, String> dict = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(dictFile));
        String line;
        while (reader.ready()) {
            line = reader.readLine();
            String[] split = line.split("\\s+");
            if (split.length == 3) {
                Set<String> set = posTagMap.get(split[2]);
                if (set != null) {
                    for (String p : set) {
                        dict.put(split[0] + "_" + p, split[1]);
                    }
                } else {
                    String failsafeTag = getFailsafeTagMorpthIt(split[2]);
                    if (failsafeTag != null) {
                        dict.put(split[0] + "_#" + failsafeTag + "#", split[1]);
                    } else {
                        dict.put(split[0] + "_#!#", split[1]);
                    }
                }
            } else {
                throw new IOException("No valid dict file, line: " + line);
            }
        }
        reader.close();
        return dict;
    }

    private Map<String, Set<String>> buildMorpht2TanlPosTagMap(File file) throws IOException {
        Map<String, Set<String>> map = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while (reader.ready()) {
            line = reader.readLine();
            String[] posSplit = line.split("\t");

            if (posSplit.length == 2) {
                String[] morphPosSplit = posSplit[1].split(" ");

                for (String mp : morphPosSplit) {
                    Set<String> set = map.get(mp);

                    if (set == null) {
                        set = new HashSet<>();
                        map.put(mp, set);
                    }
                    set.add(posSplit[0]);
                }
            } else {
                throw new IOException("No valid pos tag conversion file, line: " + line);
            }
        }

        reader.close();
        return map;
    }

}
