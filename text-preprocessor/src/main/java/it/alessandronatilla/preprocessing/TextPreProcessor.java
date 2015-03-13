package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.model.Language;
import it.alessandronatilla.preprocessing.model.StemmedWord;
import it.alessandronatilla.preprocessing.model.TaggedWord;
import it.alessandronatilla.preprocessing.stopwords.StopwordRemoval;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
public class TextPreProcessor {

    public static List<String> remove_punctuation(List<String> text) {
        List<String> result = new LinkedList<>();

        for (String word : text) {
            String token = word.replaceAll("[^a-zA-Z0-9]", "");
            if (!token.equalsIgnoreCase(""))
                result.add(token);
        }
        return result;
    }

    /*
    * removes stopwords
     */
    public static List<String> remove_stopwords(Language language, List<String> text) {

        StopwordRemoval removal = new StopwordRemoval(language);
        List<String> tokens = new LinkedList<String>();

        for (String word : text) {
            if (!removal.isStopword(word)) tokens.add(word);
        }

        return tokens;
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
    public static List<TaggedWord> tag(Language language, List<String> tokens) throws Exception {
        return OpenNLPTagger.getInstance(language).tag(tokens);
    }

    /**
     * @param token
     * @return a tagged word
     * @throws Exception
     */
    public static TaggedWord tag(Language language, String token) throws Exception {
        return OpenNLPTagger.getInstance(language).tag(token);
    }

    /**
     * @param token
     * @param posTag
     * @return
     */
    public static String lemmatize(Language language, String token, String posTag) {
        return new Lemmatizer(language).lemmatize(token, posTag);
    }

    /**
     * @param taggedWord
     * @return
     */
    public static String lemmatize(Language language, TaggedWord taggedWord) {
        return new Lemmatizer(language).lemmatize(taggedWord.getToken(), taggedWord.getPosTag());
    }

    /**
     * @param taggedWords
     * @return a stemmed word
     */
    public static List<StemmedWord> stem(Language language, List<TaggedWord> taggedWords) {
        return WordStemmerSingleton.getIstance(language).stem(taggedWords);
    }

    public static StemmedWord stem(Language language, TaggedWord taggedWord) {
        return WordStemmerSingleton.getIstance(language).stem(taggedWord);
    }

    public static String stem(Language language, String token) {
        return WordStemmerSingleton.getIstance(language).stem(token);
    }

}
