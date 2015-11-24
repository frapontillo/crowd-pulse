/*
 * Copyright 2015 Francesco Pontillo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.frakbot.crowdpulse.sentiment.sentiwordnet;

import net.frakbot.crowdpulse.common.util.StringUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Francesco Pontillo
 */
public class SentiWordNet {
    private final static Pattern mainDivider = Pattern.compile("\t");
    private final static Pattern subDivider = Pattern.compile(" ");
    private HashMap<String, SentiWord> dict;

    private HashMap<String, SentiWord> getDictionary() {
        if (dict == null) {
            dict = new HashMap<>();
            InputStream model = getClass().getClassLoader().getResourceAsStream("sentiwordnet");
            try {
                List<String> lines = IOUtils.readLines(model, Charset.forName("UTF-8"));
                lines.forEach(s -> {
                    SentiWord word = fromLine(s);
                    if (word != null) {
                        dict.put(word.getId(),word);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dict;
    }

    public SentiWord getSentiWord(String synset) {
        return getDictionary().get(synset);
    }

    public double getWeightedScore(String synset, double nounFactor, double verbFactor, double adjectiveFactor, double adverbFactor) {
        SentiWord sentiWord = getSentiWord(synset);
        if (sentiWord != null) {
            String pos = sentiWord.getId().substring(0,1);
            double factor = 1;
            if (pos.equals("n")) {
                factor = nounFactor;
            } else if (pos.equals("v")) {
                factor = verbFactor;
            } else if (pos.equals("a")) {
                factor = adjectiveFactor;
            } else if (pos.equals("r")) {
                factor = adverbFactor;
            }
            return sentiWord.getScore() * factor;
        }
        return 0;
    }

    public double getScore(String synset) {
        return getWeightedScore(synset, 1, 1, 1, 1);
    }

    public double getWeightedScore(String[] synsets, double nounFactor, double verbFactor, double adjectiveFactor, double adverbFactor) {
        if (synsets == null || synsets.length == 0) {
            return 0;
        }
        double totalScore = 0;
        for (String synset : synsets) {
            totalScore += getWeightedScore(synset, nounFactor, verbFactor, adjectiveFactor, adverbFactor);
        }
        return totalScore / synsets.length;
    }

    public double getScore(String[] synsets) {
        return getWeightedScore(synsets, 1, 1, 1, 1);
    }

    protected static SentiWord fromLine(String line) {
        line = StringUtil.leftTrim(line);
        if (line.charAt(0) == '#') {
            return null;
        }
        String[] components = mainDivider.split(line);
        if (components.length != 6) {
            return null;
        }
        SentiWord word = new SentiWord();
        word.setId(components[0] + "#" + components[1]);
        word.setPosScore(safeParseDouble(components[2]));
        word.setNegScore(safeParseDouble(components[3]));
        word.setSynsetTerms(subDivider.split(components[4]));
        word.setDefinition(components[5]);
        return word;
    }

    private static double safeParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }

    public static class SentiWord {
        private String id;
        private double posScore;
        private double negScore;
        private String[] synsetTerms;
        private String definition;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getPosScore() {
            return posScore;
        }

        public void setPosScore(double posScore) {
            this.posScore = posScore;
        }

        public double getNegScore() {
            return negScore;
        }

        public void setNegScore(double negScore) {
            this.negScore = negScore;
        }

        public double getScore() {
            return posScore - negScore;
        }

        public double getObjectivityScore() {
            return 1 - (posScore + negScore);
        }

        public String[] getSynsetTerms() {
            return synsetTerms;
        }

        public void setSynsetTerms(String[] synsetTerms) {
            this.synsetTerms = synsetTerms;
        }

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }
    }
}
