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

package net.frakbot.crowdpulse.categorize;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import rx.Observable;

import java.util.List;

/**
 * Generic implementation of a tag categorizer.
 * This task retrieves multiple Wikipedia categories from a single {@link Tag}.
 *
 * @author Francesco Pontillo
 */
public abstract class ITagCategorizer implements IPlugin {

    /**
     * Actual retrieval of categories from a {@link Tag} happens here.
     * Custom implementations must implement this method and return the appropriate values.
     *
     * @param tag The input {@link Tag} to look for categories into.
     * @return A {@link List} of {@link String}s representing the found categories.
     */
    public abstract List<String> getCategories(Tag tag);

    /**
     * Retrieves the categories from the input {@link Tag} and sets them into
     * the object itself in a {@link java.util.Set} structure.
     *
     * @param tag The input {@link Tag} to be enriched with categories.
     * @return The same input {@link Tag}, enriched with categories.
     */
    public Tag categorizeTag(Tag tag) {
        tag.addCategories(getCategories(tag));
        return tag;
    }

    /**
     * Process a stream of {@link Message} and enrich each {@link Message} {@link Tag}
     * with related categories.
     *
     * @param messages The stream of {@link Message}s to process, as {@link Observable}.
     * @return A new {@link Observable} emitting the same items once the processing has happened.
     */
    public Observable<Message> process(Observable<Message> messages) {
        return messages.lift(new TagCategorizerOperator(this));
    }
}
