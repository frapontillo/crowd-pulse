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

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Category;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * Rx {@link rx.Observable.Operator} for categorizing tags.
 *
 * @author Francesco Pontillo
 */
public abstract class ITagCategorizerOperator implements Observable.Operator<Message, Message> {
    private IPlugin plugin;

    public ITagCategorizerOperator(IPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                plugin.reportElementAsStarted(message.getId());
                if (message.getTags() != null) {
                    message.getTags().forEach(ITagCategorizerOperator.this::categorizeTag);
                }
                plugin.reportElementAsEnded(message.getId());
                subscriber.onNext(message);
            }

            @Override public void onCompleted() {
                plugin.reportPluginAsCompleted();
                super.onCompleted();
            }

            @Override public void onError(Throwable e) {
                plugin.reportPluginAsErrored();
                super.onError(e);
            }
        };
    }

    /**
     * Actual retrieval of categories from a {@link Tag} happens here.
     * Custom implementations must implement this method and return the appropriate values.
     *
     * @param tag The input {@link Tag} to look for categories into.
     * @return A {@link List} of {@link Category} representing the found categories.
     */
    public abstract List<Category> getCategories(Tag tag);

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

}
