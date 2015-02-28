package it.alessandronatilla.preprocessing;

import it.alessandronatilla.preprocessing.model.StemmedWord;
import it.alessandronatilla.preprocessing.model.TaggedWord;

import java.util.ArrayList;
import java.util.List;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.italianStemmer;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
class WordStemmer {

	private static WordStemmer self;

	private SnowballStemmer stemmer;

	private WordStemmer() {
		stemmer = new italianStemmer();
	}

	public static WordStemmer getIstance() {
		if (self == null)
			self = new WordStemmer();

		return self;
	}

	/**
	 * Returns the stem of the single token given in input.
	 * 
	 * @param token
	 *            a SINGLE token
	 * @return stem of token
	 */
	public synchronized String stem(String token) {
		stemmer.setCurrent(token);
		stemmer.stem();
		return stemmer.getCurrent();
	}

	/**
	 * Makes the stem of each TaggedWord given in input, and returns a list of
	 * StemmedWord
	 * 
	 * @param tagged
	 *            list of TaggedWord
	 * @return list of StemmedWord
	 */
	public List<StemmedWord> stem(List<TaggedWord> tagged) {

		List<StemmedWord> list = new ArrayList<StemmedWord>();
		for (TaggedWord tag : tagged) {
			String stem = stem(tag.getToken());
			list.add(new StemmedWord(tag, stem));
		}

		return list;
	}
	
	public StemmedWord stem(TaggedWord taggedWord){
		return new StemmedWord(taggedWord, stem(taggedWord.getToken()));
	}

}
