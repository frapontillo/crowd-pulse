package net.frakbot.crowdpulse.categorize;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import rx.Observable;
import rx.Subscriber;

import java.util.Set;

/**
 * Rx {@link rx.Observable.Operator} for categorizing the tags inside a {@link Message}.
 *
 * @author Francesco Pontillo
 */
public class TagCategorizerOperator implements Observable.Operator<Message, Message> {
    private final ITagCategorizer categorizer;

    public TagCategorizerOperator(ITagCategorizer categorizer) {
        this.categorizer = categorizer;
    }

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                Set<Tag> tags;
                if ((tags = message.getTags()) != null) {
                    for (Tag tag : tags) {
                        categorizer.categorizeTag(tag);
                    }
                }
                subscriber.onNext(message);
            }
        };
    }
}
