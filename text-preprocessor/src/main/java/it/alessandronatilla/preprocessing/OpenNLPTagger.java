package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.model.TaggedWord;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
class OpenNLPTagger {

    private InputStream modelIn;
    private POSModel model;
    private POSTaggerME tagger;
    private static OpenNLPTagger self;

    private OpenNLPTagger() {
        try {
            modelIn = OpenNLPTagger.class
                    .getResourceAsStream("/it-pos_perceptron.bin");
            model = new POSModel(modelIn);
            tagger = new POSTaggerME(model);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static OpenNLPTagger getInstance() {
        if (self == null)
            self = new OpenNLPTagger();
        return self;
    }

    public List<TaggedWord> tag(List<String> tokenizedText) throws Exception {
        String[] tokens = tokenizedText.toArray(new String[1]);

        List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
        for (int i = 0; i < tokens.length; i++) {
            try {
                taggedWords.add(tag(tokens[i]));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        return taggedWords;
    }

    public TaggedWord tag(String token) throws Exception {
        if (token.matches("( )+") || token.isEmpty())
            throw new Exception("The string is empty or contains spaces only");

        String[] tokens = new String[]{token};
        String[] tags = tagger.tag(tokens);

        if (tags.length == 0)
            throw new Exception("Sorry, there are no tags for this string!");

        String tag = tags[0];
        return new TaggedWord(token, tag, TextPreProcessor.lemmatize(token, tag));
    }

    @Override
    protected void finalize() throws Throwable {
        modelIn.close();
        super.finalize();
    }

}
