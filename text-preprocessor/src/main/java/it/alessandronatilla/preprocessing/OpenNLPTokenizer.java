package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.model.Language;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
class OpenNLPTokenizer {

    private Tokenizer tokenizer;
    private SentenceDetectorME sentenceDetector;
    private Language language;

    public OpenNLPTokenizer(Language language) {
        this.language = language;

        try {
            //Sentence Model
            URL resourceURL = null;
            SentenceModel sentence_model = null;

            if (language.equals(Language.IT)) {
                resourceURL = getClass().getResource("/it-sent.bin");
            } else if (language.equals(Language.EN)) {
                resourceURL = getClass().getResource("/en-sent.bin");
            }

            Path resourcePath = Paths.get(resourceURL.toURI());
            FileInputStream modelIn = new FileInputStream(resourcePath.toString());
            sentence_model = new SentenceModel(modelIn);
            sentenceDetector = new SentenceDetectorME(sentence_model);

            resourceURL = null;
            resourcePath = null;

            //Sentence tokenizer
            if (language.equals(Language.IT)) {
                resourceURL = getClass().getResource("/it-token.bin");
            } else if (language.equals(Language.EN)) {
                resourceURL = getClass().getResource("/en-token.bin");
            }
            resourcePath = Paths.get(resourceURL.toURI());

            modelIn = new FileInputStream(resourcePath.toString());
            TokenizerModel modelTokenizer = new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(modelTokenizer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public List<String> tokenize(List<String> sentences) {
        List<String> tokens_list = new LinkedList<String>();
        for (String sentence : sentences) {
            String[] tokens = tokenizer.tokenize(sentence);

            for (String token : tokens) {
                tokens_list.add(token);
            }
        }

        return tokens_list;
    }

    public List<String> get_sentences(String text) {
        String[] sentences = sentenceDetector.sentDetect(text);
        return Arrays.asList(sentences);
    }
}
