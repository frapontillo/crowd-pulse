package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.model.Language;
import it.alessandronatilla.preprocessing.model.StemmedWord;
import it.alessandronatilla.preprocessing.model.TaggedWord;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TextPreProcessorTestEN {

    private String text = "";

    @Before
    public void setUp() throws Exception {
        text = "Stanford CoreNLP provides a set of natural language analysis tools which can take raw English language text input and give the base forms of words, their parts of speech, whether they are names of companies, people, etc., normalize dates, times, and numeric quantities, mark up the structure of sentences in terms of phrases and word dependencies, and indicate which noun phrases refer to the same entities. It provides the foundational building blocks for higher level text understanding applications. ";
    }

    @Test
    public void testClean() throws Exception {
        List<String> sentences = TextPreProcessor.segment(Language.EN, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.EN, sentences);
        List<String> clear_tokens = TextPreProcessor.remove_stopwords(Language.EN, tokens);

        assert (clear_tokens.size() < tokens.size());
    }

    @Test
    public void testPunctuationRemoval() {
        List<String> sentences = TextPreProcessor.segment(Language.EN, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.EN, sentences);
        List<String> nopuct = TextPreProcessor.remove_punctuation(tokens);

        assert (nopuct.size() < tokens.size());
    }

    @Test
    public void testStemming() throws Exception {
        List<String> sentences = TextPreProcessor.segment(Language.EN, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.EN, sentences);
        List<String> no_puct_tokens = TextPreProcessor.remove_punctuation(tokens);
        List<String> clear_tokens = TextPreProcessor.remove_stopwords(Language.EN, no_puct_tokens);
        List<TaggedWord> words = TextPreProcessor.tag(Language.EN, clear_tokens);
        List<StemmedWord> stemmedWords = TextPreProcessor.stem(Language.EN, words);

        assert(stemmedWords.size()>0);
    }

    @Test
    public void testLemmatization(){

    }



}