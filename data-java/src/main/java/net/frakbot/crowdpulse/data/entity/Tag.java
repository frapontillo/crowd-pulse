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

import java.util.*;

/**
 * Semantic tag for {@link Message}s.
 *
 * @author Francesco Pontillo
 */
public class Tag extends GenericEntity<String> {
    private List<String> sources;
    private String language;
    private Set<String> categories;

    /**
     * Get the text of the Tag.
     *
     * @return The text of the Tag.
     */
    public String getText() {
        return getId();
    }

    /**
     * Set the text of the Tag.
     *
     * @param text The text of the Tag.
     */
    public void setText(String text) {
        setId(text);
    }

    /**
     * Get the sources where the Tag was generated from.
     *
     * @return The sources of the Tag.
     */
    public List<String> getSources() {
        return sources;
    }

    /**
     * Set the sources where the Tag was generated from.
     *
     * @param sources The sources of the Tag.
     */
    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    /**
     * Add an origin source for the Tag.
     *
     * @param source A new source for the Tag.
     */
    public void addSource(String source) {
        if (this.sources == null) {
            this.sources = new ArrayList<>();
        }
        this.sources.add(source);
    }

    /**
     * Get the language of the Tag.
     *
     * @return The language of the Tag.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language of the Tag.
     *
     * @param language The language of the Tag.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Get the {@link Set} of categories associated to the Tag.
     *
     * @return The categories of the Tag.
     */
    public Set<String> getCategories() {
        return categories;
    }

    /**
     * Change the {@link Set} of categories associated to the Tag.
     *
     * @param categories The categories of the Tag.
     */
    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    /**
     * Increment the current {@link Set} of categories with new ones.
     *
     * @param categories The {@link Set} of categories to add to the Tag.
     */
    public void addCategories(Set<String> categories) {
        if (this.categories == null) {
            this.categories = categories;
        } else {
            this.categories.addAll(categories);
        }
    }

    /**
     * Increment the current {@link Set} of categories with new ones.
     *
     * @param categories The {@link List} of categories to add to the Tag.
     */
    public void addCategories(List<String> categories) {
        if (categories != null) {
            addCategories(new HashSet<>(categories));
        }
    }

    /**
     * Compare two Tags and check for equality based on Tag ids.
     *
     * @param other The Tag to compare to the current one.
     * @return {@code true} if two Tags are equal, {@code false} otherwise.
     */
    @Override public boolean equals(Object other) {
        try {
            Tag otherTag = (Tag) other;
            return Objects.equals(this.getId(), otherTag.getId());
        } catch (ClassCastException e) {
            return super.equals(other);
        }
    }

    /**
     * Get the hashcode of the Tag, delegating the calculation to the {@link #id#hashCode()} method.
     *
     * @return The hashcode of the Tag.
     */
    @Override public int hashCode() {
        return Objects.hashCode(this.getId());
    }
}
