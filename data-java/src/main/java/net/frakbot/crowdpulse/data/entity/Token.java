/*
 * Copyright 2015 Francesco Pontillo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.frakbot.crowdpulse.data.entity;

/**
 * Tokens are part of the {@link Message} text.
 *
 * @author Francesco Pontillo
 */
public class Token {
    private String text;
    private String pos;
    private String simplePos;
    private boolean stopWord;
    private String lemma;
    private double score;

    public Token() {
    }

    /**
     * Create a new Token and assign it a text.
     *
     * @param text The text of the Token.
     */
    public Token(String text) {
        this.text = text;
    }

    /**
     * Get the text of the Token.
     *
     * @return The text of the Token.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text for the Token.
     *
     * @param text The text for the Token.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get the Part-of-Speech associated to the Token.
     *
     * @return The Part-of-Speech associated to the Token.
     */
    public String getPos() {
        return pos;
    }

    /**
     * Set a Part-of-Speech to associate to the Token.
     *
     * @param pos The Part-of-Speech to associate to the Token.
     */
    public void setPos(String pos) {
        this.pos = pos;
    }

    /**
     * Get a simpler version of the Part-of-Speech. There's no restriction on the validity of the simple POS, but
     * implementations <i>should</i> use the following simpler POS: "n", "v", "a", "r".
     *
     * @return A simpler version of the POS.
     */
    public String getSimplePos() {
        return simplePos;
    }

    /**
     * Set a simpler version of the Part-of-Speech. There's no restriction on the validity of the simple POS, but
     * implementations <i>should</i> use the following simpler POS: "n", "v", "a", "r".
     *
     * @param simplePos A simpler version of the POS.
     */
    public void setSimplePos(String simplePos) {
        this.simplePos = simplePos;
    }

    /**
     * Check if the Token is considered a stop word.
     *
     * @return {@code true} if the Token is a stop word, {@code false} otherwise.
     */
    public boolean isStopWord() {
        return stopWord;
    }

    /**
     * Mark the Token as a stop word or not.
     *
     * @param stopWord {@code true} if the Token is a stop word, {@code false} otherwise.
     */
    public void setStopWord(boolean stopWord) {
        this.stopWord = stopWord;
    }

    /**
     * Get the lemmatized version of the Token.
     *
     * @return The lemmatized version of the Token.
     */
    public String getLemma() {
        return lemma;
    }

    /**
     * Set the lemmatized version of the Token.
     *
     * @param lemma A lemmatized version of the Token.
     */
    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    /**
     * Get the sentiment score assigned to the Token.
     *
     * @return The sentiment of the single Token.
     */
    public double getScore() {
        return score;
    }

    /**
     * Set a sentiment score for the Token.
     *
     * @param score The new sentiment score of the single Token.
     */
    public void setScore(double score) {
        this.score = score;
    }
}
