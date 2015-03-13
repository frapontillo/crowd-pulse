package it.alessandronatilla.preprocessing;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
class WordTokenizer {

    /**
     * Returns the list of tokens detected in the sentence given in input.
     *
     * @param text raw text
     * @return tokens detected in the text
     */
    public static List<String> tokenize(String text) {

        List<String> tokens = new ArrayList<String>();

        BreakIterator boundary = BreakIterator.getWordInstance(Locale.ITALIAN);
        boundary.setText(text);

        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary
                .next()) {
            tokens.add(text.substring(start, end));
        }

        return tokens;
    }
}
