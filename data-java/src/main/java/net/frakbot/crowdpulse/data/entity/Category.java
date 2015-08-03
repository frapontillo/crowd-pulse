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
 * Category for {@link Tag} elements
 *
 * @author Francesco Pontillo
 */
public class Category {
    private String text;
    private boolean stopWord;

    public Category(String text) {
        this.text = text;
        this.stopWord = false;
    }

    /**
     * Get the text of the Category.
     *
     * @return The text of the Category.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of the Category.
     *
     * @param text The text of the Category.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Check if the Tag is considered a stop word.
     *
     * @return {@code true} if the Tag is a stop word, {@code false} otherwise.
     */
    public boolean isStopWord() {
        return stopWord;
    }

    /**
     * Mark the Tag as a stop word or not.
     *
     * @param stopWord {@code true} if the Tag is a stop word, {@code false} otherwise.
     */
    public void setStopWord(boolean stopWord) {
        this.stopWord = stopWord;
    }
}
