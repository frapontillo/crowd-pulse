package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.model.Language;
import it.alessandronatilla.preprocessing.model.StemmedWord;
import it.alessandronatilla.preprocessing.model.TaggedWord;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Author: alexander
 * Project: textpreprocessor
 */

public class TextPreProcessorTestIT {

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
    public void tokenize() {
        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.IT, segments);

        assert (tokens.size() > 0);

    }

    @Test
    public void tag() throws Exception {
        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.IT, segments);
        List<TaggedWord> words = TextPreProcessor.tag(Language.IT, tokens);

        assert (words.size() > 0);
    }

    @Test
    public void stem() throws Exception {
        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.IT, segments);
        List<TaggedWord> words = TextPreProcessor.tag(Language.IT, tokens);
        List<StemmedWord> stemmedWords = TextPreProcessor.stem(Language.IT, words);
        System.out.println(stemmedWords);

        assert (stemmedWords.size() > 0);
    }

    @Test
    public void lemmatization() throws UnsupportedEncodingException {

        String word = "già";
        System.out.println("già");
        String postag = "Bis";
        String lemma = TextPreProcessor.lemmatize(Language.IT, word, postag);
        System.out.println("Lemma for " + word + " is: " + lemma);
        assert (lemma != null);
    }

    @Test
    public void complete_workflowIT() throws Exception {
        Long tot = 0l;

        System.out.println("TEST ON TEXT 1");
        Long start_ms = System.currentTimeMillis();
        pipelineIT(text);
        Long final_ms = System.currentTimeMillis() - start_ms;
        System.out.println("\t elapsed: " + final_ms+" ms" );
        tot += final_ms;

        System.out.println("TEST ON TEXT 2");
        text = "Come ampiamente annunciato, la nuova penalizzazione e l'inibizione dell'ex presidente e dell'ex amministratore delegato del club ducale per i mancati pagamenti ai propri tesserati: \"Ghirardi e Leonardi erano stati deferiti per non aver depositato presso la CO.VI.SO.C., entro il termine del 17 novembre, la dichiarazione attestante l’avvenuto pagamento degli emolumenti dovuti ai propri tesserati, lavoratori dipendenti e collaboratori addetti al settore sportivo per le mensilità di luglio, agosto e settembre 2014 nonché per non aver depositato la dichiarazione attestante l’avvenuto pagamento delle ritenute Irpef e dei contributi Inps relativi agli emolumenti dovuti ai propri tesserati, lavoratori dipendenti e collaboratori addetti al settore sportivo per le mensilità di luglio, agosto e settembre 2014\".";
        Long start_ms1 = System.currentTimeMillis();
        pipelineIT(text);
        Long final_ms1 = System.currentTimeMillis() - start_ms1;
        System.out.println("\t elapsed: " + final_ms1 +" ms");
        tot += final_ms1;

        System.out.println("TEST ON TEXT 3");
        text = "Nei giorni scorsi i nomi di Yaya Tourè e Stevan Jovetic sono stati ripetutamente accostati all'Inter, anche in seguito alle parole di Mancini, che apriva una porta preferenziale all'arrivo dei due Citizens in nerazzurro. Il tecnico del Manchester City, Manuel Pellegrini, ha però spento ogni ipotesi di trasferimento.\n" +
                "\n" +
                "Intervenuto in conferenza stampa alla vigilia della sfida contro il Burnley, Pellegrini si sofferma sulle parole del tecnico dell'Inter, che mandava messaggi eloquenti in relazione ai due giocatori: \"Io non so come Mancini possa avere così tante informazioni su di loro\".\n" +
                "\n" +
                "Secondo Pellegrini, Tourè e Jovetic resteranno sicuramente in Premier League: \"Stevan Jovetic, come me, ha un contratto qui, mentre su Touré ci sono notizie da inizio anno. Tutti vorrebbero avere Yaya nella loro squadra, ma lui ha un contratto qui, è felice e continuerà al City\".";
        Long start_ms2 = System.currentTimeMillis();
        pipelineIT(text);
        Long final_ms2 = System.currentTimeMillis() - start_ms2;
        System.out.println("\t elapsed: " + final_ms2 +" ms");
        tot += final_ms2;

        System.out.println("TEST ON TEXT 4");
        text = "Oltre due milioni di euro, tra assegni e bonifici, sono stati versati da Silvio Berlusconi alle ragazze ospiti delle serate di Arcore, Ruby esclusa, nel periodo compreso tra il 2010 e i primi mesi del 2014. E' quanto si evince dagli atti della Procura di Milano che riguardano l'inchiesta 'Ruby ter' e che sono stati appena depositati al tribunale del Riesame. I magistrati che indagano hanno da poco chiesto una proroga delle indagini per poter analizzare la \"mole\" di indizi raccolti sul caso.\n" +
                "\n" +
                "Il premier, unica fonte di reddito. Le indagini bancarie eseguite fino ad ora porterebbero a dire che non sono documentate fonti di reddito \"delle indagate che non siano riconducibili in modo diretto e indiretto\" a Berlusconi che stato assolto in terzo grado per il primo filone d'indagine, il caso Ruby. Gli accertamenti bancari sono stati disposti dai magistrati sui conti correnti di una ventina di 'olgettine' da poco depositati al Riesame. Le ragazze tra assegni e bonifici, dal 2010 al 2014, avrebbero ricevuto una somma che, per la precisione, è stata fissata in 2 milioni e 150 mila euro.\n" +
                "\n" +
                "Una 'liquidazione'  da 25mila euro. Agli atti c'è anche la lettera di 'congedo' scritta da Berlusconi nel dicembre 2013 alle ragazze. Nella missiva l'ex premier scrive, in sostanza,  di non poter più aiutare le giovani in quanto questo suo gesto di generosità è stato interpretato in termini negativi e porterebbe guai per tutti. Però annuncia di lasciare ad alcune di loro, in tutto 14 ragazze, un ultimo aiuto e cioè una sorta di buona uscita di 25 mila euro.\n" +
                "\n" +
                "Spinelli chiede istruzioni. Sempre agli atti dell'indagine, c'è anche il verbale del ragioniere Giuseppe Spinelli, sentito quattro volte dagli inquirenti. Spinelli (l'uomo incaricato da Silvio Berlusconi di elargire gli 'aiuti') racconta di essersi trovato di fronte a ragazze che gli dicevano di essere in difficoltà economiche quando gli chiedevano il denaro. Nelle sue deposizioni, il ragioniere Spinelli spiega di avere avuto un budget di 2.500 euro mensili. Nel caso in cui le ragazze avessero chiesto altro, il ragioniere  ha raccontato agli inquirenti di avere chiesto l'autorizzazione a  Berlusconi per procedere a ulteriori versamenti.\n" +
                "\n" +
                "La contabilità. Sempre stando alle analisi bancarie, le gemelle, Eleonora e Concetta De Vivo avrebbero ricevuto 100 mila euro ciascuna e Barbara Faggioli 180 mila, la giornalista Silvia Trevaini 55 mila euro. Tutto sempre da conti riconducibili a Silvio Berlusconi.";
        Long start_ms3 = System.currentTimeMillis();
        pipelineIT(text);
        Long final_ms3 = System.currentTimeMillis() - start_ms3;
        System.out.println("\t elapsed: " + final_ms3 +" ms");

        tot += final_ms3;
        System.out.println("Avg time: " + tot / 4);
    }

    private List<StemmedWord> pipelineIT(String text) throws Exception {

        List<String> segments = TextPreProcessor.segment(Language.IT, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.IT, segments);
        List<String> no_punct_tokens = TextPreProcessor.remove_punctuation(tokens);
        List<String> clean_tokens = TextPreProcessor.remove_stopwords(Language.IT, no_punct_tokens);
        List<TaggedWord> words = TextPreProcessor.tag(Language.IT, clean_tokens);
        List<StemmedWord> stemmedWords = TextPreProcessor.stem(Language.IT, words);
        System.out.println("\t Words:" + words.size());
        return stemmedWords;
    }
}
