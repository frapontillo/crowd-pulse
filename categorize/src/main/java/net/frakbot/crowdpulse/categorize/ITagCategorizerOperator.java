package net.frakbot.crowdpulse.categorize;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Tag;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * Rx {@link rx.Observable.Operator} for categorizing tags.
 *
 * @author Francesco Pontillo
 */
public abstract class ITagCategorizerOperator implements Observable.Operator<Tag, Tag> {

    @Override
    public Subscriber<? super Tag> call(Subscriber<? super Tag> subscriber) {
        return new CrowdSubscriber<Tag>(subscriber) {
            @Override
            public void onNext(Tag tag) {
                categorizeTag(tag);
                subscriber.onNext(tag);
            }
        };
    }

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

}
