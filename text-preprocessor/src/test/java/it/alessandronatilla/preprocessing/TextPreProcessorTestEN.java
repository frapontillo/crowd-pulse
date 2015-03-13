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
    public void complete_workflowEN() throws Exception {
        System.out.println("TEST ON TEXT 1");
        Long tot = 0l;
        Long start_ms = System.currentTimeMillis();
        pipelineEN(text);
        Long final_ms = System.currentTimeMillis() - start_ms;
        tot += final_ms;
        System.out.println("\t elapsed: " + final_ms + " ms");

        text = "I learned a lot about Google Website Optimizer in a presentation at Net Tuesday Vancouver earlier this week. I also have a write-up on Adsense from the same event, if you're interested.\n" +
                "\n" +
                "VKI Studios' John Hossack was the speaker. His presentation focused on the \"post-click\" portion of online advertising. It starts from when a user clicks on an advertisement and ends up in your website.\n" +
                "\n" +
                "The goal of this process is to get the user to perform some action, like purchasing your product, making a donation, or telling their friends about your product or service. There are many different ways you can encourage a visitor to take action. Different wording, button styles, product images, and layouts all have an effect.\n" +
                "\n" +
                "So how can Google help? One way to decide which button style makes more users want to click it is to use your experience or intuition. Another way is to test, test, test. And that's where Website Optimizer comes in.\n" +
                "What Website Optimizer Does\n" +
                "\n" +
                "Website Optimizer a free service from Google that allows you to present different versions of your website to users. Each time somebody visits your website they'll see one of your test cases. If you're testing button styling, one test case might be a yellow button and another case a red button.\n" +
                "\n" +
                "Web Optimizer records which version of the website was displayed and then records whether or not the user clicked the button. This button click is your \"success event\" and should lead to one of the desired actions mentioned above.\n" +
                "\n" +
                "Once you get enough of these \"success events\" you can look at the numbers to determine which button was more likely to be clicked. By testing different improvements all over your website, you can make it more effective at getting visitors to perform the desired action.\n" +
                "Notes on Website Optimizer\n" +
                "\n" +
                "There's a caveat to all this. Your site needs to have visitors coming in and triggering \"success events\" fairly regularly for this to be useful. If only ten people click the button in a month then you don't have very much data to measure. John recommends having at least 100 success events per test case per month.\n" +
                "\n" +
                "\"Test shouts, not whispers.\"\n" +
                "\n" +
                "John also points out that you should be testing big changes. He likes to use the six-foot rule: you should be testing changes that are visible from six feet away from the monitor.\n" +
                "\n" +
                "I have to say I learned a lot from both speakers at Net Tuesday. Many thanks to the speakers! Slides for both presentations are now available to Net Tuesday Meetup members.";

        System.out.println("TEST ON TEXT 2");
        start_ms = 0l;
        start_ms = System.currentTimeMillis();
        pipelineEN(text);
        final_ms = System.currentTimeMillis() - start_ms;
        tot += final_ms;
        System.out.println("\t elapsed: " + final_ms + " ms");

        text = "My name is Daniel and I'm a polling addict. Anyone who works on a computer connected to the internet knows what I'm talking about. It's those compulsive visits to the likes of Craigslist, eBay, and even services like Google Analytics to see if there's been any change. We're waiting for new job postings or new bids or a slew of new visits from a referring website. Each of these tools definitely has its use but we polling addicts check on them with pointless frequency.";
        System.out.println("TEST ON TEXT 3");
        start_ms = 0l;
        start_ms = System.currentTimeMillis();
        pipelineEN(text);
        final_ms = System.currentTimeMillis() - start_ms;
        tot += final_ms;
        System.out.println("\t elapsed: " + final_ms + " ms");

        text = "There are basically two situations when this behavior kicks in. When you're procrastinating from some big task and when you're bored. Procrastinating isn't so bad because visiting a website to see if anything's changed doesn't take long. However, you might poll the same websites several times while milking other distractions before getting down to business.\n" +
                "\n" +
                "The worst trigger for polling addiction is short-term boredom. Whenever you have to wait a few seconds for a code compile or a slow website you might \"just quickly check\" whether your Flickr stats have changed.\n" +
                "\n" +
                "I know that when I see a progress bar for more than a second I impulsively switch tabs or windows to continue with another task. Sometimes, though, it means I'll go check couch prices on Craigslist and spend much more time there than it took for the progress bar to finish.\n" +
                "\n" +
                "So why do we do this? It's your basic reward system. If there is a change, that is, if a new couch has appeared or a new comment or a new bid, then we get a reward. Part of that reward comes from the novelty of the new item but it can also be tied to getting attention from others or even financial/success-related.";
        System.out.println("TEST ON TEXT 4");
        start_ms = 0l;
        start_ms = System.currentTimeMillis();
        pipelineEN(text);
        final_ms = System.currentTimeMillis() - start_ms;
        tot += final_ms;
        System.out.println("\t elapsed: " + final_ms + " ms");

        System.out.println("Avg time: " + tot / 4 + "ms");
    }


    public List<StemmedWord> pipelineEN(String text) throws Exception {
        List<String> sentences = TextPreProcessor.segment(Language.EN, text);
        List<String> tokens = TextPreProcessor.tokenize(Language.EN, sentences);
        List<String> no_puct_tokens = TextPreProcessor.remove_punctuation(tokens);
        List<String> clear_tokens = TextPreProcessor.remove_stopwords(Language.EN, no_puct_tokens);
        List<TaggedWord> words = TextPreProcessor.tag(Language.EN, clear_tokens);
        List<StemmedWord> stemmedWords = TextPreProcessor.stem(Language.EN, words);
        System.out.println("\t Words:" + words.size());
        return stemmedWords;
    }


}