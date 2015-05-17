package net.frakbot.crowdpulse.tag;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class ITaggerOperator implements Observable.Operator<Message, Message> {

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = tagMessage(message);
                subscriber.onNext(message);
            }
        };
    }

    /**
     * Starts an asynchronous tagging process loading an {@link List} of
     * {@link net.frakbot.crowdpulse.data.entity.Tag}.
     *
     * @param text {@link String} text to tag
     * @param language {@link String} language of the text to tag (can be discarded by some implementations)
     * @return {@link List <net.frakbot.crowdpulse.data.entity.Tag>}
     */
    public List<Tag> getTags(String text, String language) {
        List<Tag> tags = getTagsImpl(text, language);
        for (Tag tag : tags) {
            tag.setLanguage(language);
        }
        return tags;
    }

    public Message tagMessage(Message message) {
        List<Tag> tags = getTags(message.getText(), message.getLanguage());
        message.addTags(tags);
        return message;
    }

    /**
     * Actual {@link Tag} retrieval implementation.
     *
     * @param text      The text to add {@link Tag}s to.
     * @param language  The language of the text.
     * @return          A {@link List<Tag>}.
     */
    protected abstract List<Tag> getTagsImpl(String text, String language);

}
