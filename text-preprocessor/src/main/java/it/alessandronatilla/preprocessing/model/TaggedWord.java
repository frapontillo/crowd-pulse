package it.alessandronatilla.preprocessing.model;

/**
 * Author: alexander
 * Project: text-preprocessor
 */
public class TaggedWord {

	/**
	 * token is the word extracted from the sentence
	 */
	protected String token;

	/**
	 * posTag is the part of speech tag of the token
	 */
	protected String posTag;

	/**
	 * lemmatizer only removes affixes
	 */
	protected String lemma;

	public TaggedWord(String token, String posTag, String lemma) {
		this.token = token.trim();
		this.posTag = posTag;
		this.lemma = lemma;
	}

	protected TaggedWord() {
	}

	/**
	 * Returns the value of the token
	 * 
	 * @return value of token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Set the value of attribute token
	 * 
	 * @param token
	 *            value of token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Returns the value of part of speech tagging
	 * 
	 * @return value of pos-tagging
	 */
	public String getPosTag() {
		return posTag;
	}

	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}

	/**
	 * Returns the value of lemma
	 * 
	 * @return value of lemma
	 */
	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	@Override
	public String toString() {
		return "TaggedWord [token=" + token + ", posTag=" + posTag + ", lemma="
				+ lemma + "]";
	}
}
