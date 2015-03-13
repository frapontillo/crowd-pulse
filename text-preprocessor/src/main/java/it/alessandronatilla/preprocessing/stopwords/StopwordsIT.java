package it.alessandronatilla.preprocessing.stopwords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

class StopwordsIT {

	private static Set<String> words = new HashSet<String>();

	private static String file = "/stopwordsIT.txt";

	static {

		try {
			loadStopWordsIT();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadStopWordsIT() throws URISyntaxException, IOException {
		InputStream istream = StopwordsIT.class.getResourceAsStream(file);
		BufferedReader in = new BufferedReader(new InputStreamReader(istream,
				StandardCharsets.UTF_8));

		String line = null;
		while ((line = in.readLine()) != null) {
			// String[] split = line.split("\\|");
			String w = line.trim();
			words.add(w);
//			words.add(WordStemmer.getInstance().stem(w));
		}
		in.close();
	}

	public static Set<String> getWords() {
		return words;
	}

}
