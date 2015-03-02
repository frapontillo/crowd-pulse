package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.model.Language;
import it.alessandronatilla.preprocessing.model.StemmedWord;
import it.alessandronatilla.preprocessing.model.TaggedWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
public class TextPreProcessor {
    /*
    * Cleans text
     */
    public static List<String> clean(Language language, String text) {

        return null;
    }

    /**
     * @param text
     * @return a list of sentences
     */
//	public static List<String> segment(String text) {
//		return SentenceSegmentator.segment(text);
//	}
    public static List<String> segment(Language language, String text) {
        return new OpenNLPTokenizer(language).get_sentences(text);
    }

    /**
     * @param sentences
     * @return the list of tokens for the given sentence
     */

    public static List<String> tokenize(Language language, List<String> sentences) {
        List<String> tokens = new ArrayList<String>();
        tokens.addAll(new OpenNLPTokenizer(language).tokenize(sentences));

        return tokens;
    }

    /**
     * @param tokens
     * @return a list of tagged words
     * @throws Exception
     */
    public static List<TaggedWord> tag(List<String> tokens) throws Exception {
        return OpenNLPTagger.getIstance().tag(tokens);
    }

    /**
     * @param token
     * @return a tagged word
     * @throws Exception
     */
    public static TaggedWord tag(String token) throws Exception {
        return OpenNLPTagger.getIstance().tag(token);
    }

    /**
     * @param token
     * @param posTag
     * @return
     */
    public static String lemmatize(String token, String posTag) {

//        Dictionary dictionary = DictionaryFactory.getDictionary();
//        dictionary.getLemmas(token, posTag);
        return new Lemmatizer().lemmatize(token, posTag);

    }

    /**
     * @param taggedWord
     * @return
     */
    public static String lemmatize(TaggedWord taggedWord) {
        return null;
    }

    /**
     * @param taggedWords
     * @return a stemmed word
     */
    public static List<StemmedWord> stem(List<TaggedWord> taggedWords) {
        return WordStemmer.getIstance().stem(taggedWords);
    }

    public static StemmedWord stem(TaggedWord taggedWord) {
        return WordStemmer.getIstance().stem(taggedWord);
    }

    public static String stem(String token) {
        return WordStemmer.getIstance().stem(token);
    }

}
