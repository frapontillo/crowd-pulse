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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

        String text1 = "Il volo è durato tre partite, poi la caduta fragorosa, più nel risultato che nel gioco, ma che in pratica mette la parola fine alle residue speranze di terzo posto e allontana anche i piazzamenti che valgono l'Europa meno prestigiosa. Ieri a San Siro è andata in scena una sfida tra una squadra che sta ancora studiando per diventare grande (l'Inter) e una (la Fiorentina) che ha trovato la quadratura del cerchio e la fiducia derivante dal lungo filotto di gare senza conoscere sconfitta.\n" +
                "\n" +
                "E' vero che nell'assalto finale con la Viola in inferiorità numerica (prima in 10 e poi in 9) ci sarebbe anche potuto scappare il pareggio, ma se c'era una squadra che meritava i tre punti senza dubbio questa era la Fiorentina. Squadra solida, che gioca a memoria e che, nonostante le tante assenze (tra infortuni e scelte tecniche), non perde mai la propria identità, proponendo un calcio offensivo che è una delizia per gli occhi.\n" +
                "\n" +
                "Una grande del nostro campionato, senza dubbio, che lotterà fino alla fine per il terzo posto (e magari qualcosa di più). Obiettivo ormai precluso, invece, a Guarin e compagni che sono incappati in una sconfitta contro una diretta concorrente e devono riporre nel cassetto i sogni di gloria. Il lavoro che attende Mancini è mastodontico, anche perché i nerazzurri in questa stagione non hanno ancora mai battuto una big.";
        text = new String(text1.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
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
    public void lemmatization() {
        String word = "abbarbichiamo";
        String postag = "VMis";
        String lemma = TextPreProcessor.lemmatize(word, postag);
        System.out.println("Lemma for " + word + " is: " + lemma);
        assert (lemma.length() > 0);
    }
}
