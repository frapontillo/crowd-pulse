import it.alessandronatilla.preprocessing.TextPreProcessor;
import it.alessandronatilla.preprocessing.model.Language;
import it.alessandronatilla.preprocessing.model.StemmedWord;
import it.alessandronatilla.preprocessing.model.TaggedWord;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

    String text = "";

    @Before
    public void init() {

        text = "Quando girano loro, insomma, gira tutto, ma se loro non vanno ne risente tutta la squadra. Con l'arrivo del tecnico jesino, il colombiano è tornato quello dei tempi del Porto: ha segnato 3 goal (6 quelli totali), fornito 5 assist per i compagni, ed è diventato il giocatore nerazzurro che ha tentato più volte il tiro in porta (29 volte, come Icardi) e il passaggio filtrante. Una vera e propria trasformazione, di cui la squadra ha chiaramente beneficiato.";
    }


    @Test
    public void segment() {
        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        System.out.println(segments);
        assert (segments.size() > 0);
    }

    @Test
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

    @Test
    public void lemmatization() throws UnsupportedEncodingException {

        String word = "già";
        System.out.println("già");
        String postag = "Bis";
        String lemma = TextPreProcessor.lemmatize(word, postag);
        System.out.println("Lemma for " + word + " is: " + lemma);
        assert (lemma != null);
    }

    @Test
    public void complete_workflow() throws Exception {
        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.IT, segments);
        List<String> clean_tokens = TextPreProcessor.clean(Language.IT, tokens);
        List<TaggedWord> words = TextPreProcessor.tag(clean_tokens);
        List<StemmedWord> stemmedWords = TextPreProcessor.stem(words);

        assert (tokens.size() >= clean_tokens.size());
    }
}
