package it.alessandronatilla.preprocessing.model;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
public class StemmedWord extends TaggedWord {

	/**
	 * The stem of the token
	 */
	protected String stem;

	public StemmedWord(TaggedWord tagWord, String stem) {
		super(tagWord.token, tagWord.posTag, tagWord.lemma);
		this.stem = stem;
	}

	protected StemmedWord() {
		super();
	}

	/**
	 * Returns the stem of this stemmedWord
	 * 
	 * @return stem
	 */
	public String getStem() {
		return stem;
	}

	/**
	 * Set the stem of this stemmedWord
	 * 
	 * @param stem
	 *            stem of the token
	 */
	public void setStem(String stem) {
		this.stem = stem;
	}

	@Override
	public String toString() {
		return "StemmedWord [ token=" + token + ", posTag=" + posTag
				+ ", lemma=" + lemma + "stem=" + stem + "]";
	}

}
