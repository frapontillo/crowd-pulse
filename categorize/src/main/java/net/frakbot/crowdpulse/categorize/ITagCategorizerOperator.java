package net.frakbot.crowdpulse.categorize;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
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

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                if (message.getTags() != null) {
                    message.getTags().forEach(ITagCategorizerOperator.this::categorizeTag);
                }
                subscriber.onNext(message);
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
