package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.exceptions.UnsupportedLanguageException;
import it.alessandronatilla.preprocessing.model.Language;
import it.alessandronatilla.preprocessing.model.StemmedWord;
import it.alessandronatilla.preprocessing.model.TaggedWord;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.italianStemmer;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
class WordStemmerSingleton {

    private static WordStemmerSingleton self;

    private SnowballStemmer stemmer;

    private WordStemmerSingleton(Language language) {
        if (language.equals(Language.IT))
            stemmer = new italianStemmer();

        else if (language.equals(Language.EN))
            stemmer = new englishStemmer();

        else throw new UnsupportedLanguageException("Language not supported");
    }

    public static WordStemmerSingleton getIstance(Language language) {
        if (self == null)
            self = new WordStemmerSingleton(language);

        return self;
    }

    /**
     * Returns the stem of the single token given in input.
     *
     * @param token a SINGLE token
     * @return stem of token
     */
    public synchronized String stem(String token) {
        stemmer.setCurrent(token);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    /**
     * Makes the stem of each TaggedWord given in input, and returns a list of
     * StemmedWord
     *
     * @param tagged list of TaggedWord
     * @return list of StemmedWord
     */
    public List<StemmedWord> stem(List<TaggedWord> tagged) {

        List<StemmedWord> list = new ArrayList<StemmedWord>();
        for (TaggedWord tag : tagged) {
            String stem = stem(tag.getToken());
            list.add(new StemmedWord(tag, stem));
        }

        return list;
    }

    public StemmedWord stem(TaggedWord taggedWord) {
        return new StemmedWord(taggedWord, stem(taggedWord.getToken()));
    }

}
