import it.alessandronatilla.preprocessing.TextPreProcessor;
import it.alessandronatilla.preprocessing.model.Language;
import it.alessandronatilla.preprocessing.model.StemmedWord;
import it.alessandronatilla.preprocessing.model.TaggedWord;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Author: alexander
 * Project: textpreprocessor
 */

public class TextPreProcessorTest {
    String text = "Il tecnico del Parma, Donadoni, e il capitano della squadra, Lucarelli, in conferenza stampa spiegano il disagio e la situazione critica in cui versa il club emiliano.";

    @Test
    public void segment() {
        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        System.out.println(segments);
        assert (segments.size() > 0);
    }


    public void token_exists() throws URISyntaxException, IOException {
        URL resourceURL = getClass().getResource("/it-sent.bin");
        Path resourcePath = Paths.get(resourceURL.toURI().getPath());

        InputStream modelIn = getClass().getResourceAsStream("/it-sent.bin");

//        System.out.println(resourcePath.toString());
//        System.out.println(new SentenceDetectorME(new SentenceModel(new FileInputStream("src/main/resources/it-sent.bin"))));


        System.out.println(new SentenceDetectorME(new SentenceModel(modelIn)));
    }

    @Test
    public void tokenize() {
        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.IT, segments);

        assert (tokens.size() > 0);

    }

    @Test
    public void tag() throws Exception {
        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.IT, segments);
        List<TaggedWord> words = TextPreProcessor.tag(tokens);

        assert (words.size() > 0);
    }

    @Test
    public void stem() throws Exception {
        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.IT, segments);
        List<TaggedWord> words = TextPreProcessor.tag(tokens);
        List<StemmedWord> stemmedWords = TextPreProcessor.stem(words);
        System.out.println(stemmedWords);

        assert (stemmedWords.size() > 0);

    }

    private String getFile(String fileName) {

        String result = "";

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }
}
