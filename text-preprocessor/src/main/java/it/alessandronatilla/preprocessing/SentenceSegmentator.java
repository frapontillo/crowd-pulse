package it.alessandronatilla.preprocessing;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
class SentenceSegmentator {

	/**
	 * Returns all sentences detected in the text as a list of separated string.
	 * 
	 * @param text
	 *            raw text
	 * @return the sentences in the text with the dot, question mark, bang and
	 *         so on at the end
	 */
	public static List<String> segment(String text) {

		List<String> sentences = new ArrayList<String>();

		BreakIterator boundary = BreakIterator
				.getSentenceInstance(Locale.ITALIAN);
		boundary.setText(text);

		int start = boundary.first();
		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary
				.next()) {
			sentences.add(text.substring(start, end));
		}

		return sentences;
	}
}
