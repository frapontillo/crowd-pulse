package net.frakbot.crowdpulse.tokenize;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class ITokenizerOperator implements Observable.Operator<Message, Message> {

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = tokenize(message);
                subscriber.onNext(message);
            }
        };
    }

    /**
     * Tokenize the input {@link Message} into chunks represented by {@link Token}s.
     * No extra information will be stored into the retrieved elements except for the {@link Token#text}.
     *
     * @param message The {@link Message} to process.
     * @return a {@link List} of simple {@link Token}s.
     */
    public abstract List<Token> getTokens(Message message);

    /**
     * Retrieve all the {@link Token}s of the {@link Message}, then sets them to the {@link Message} itself.
     *
     * @param message The {@link Message} to process.
     * @return the {@link Message} with newly added {@link Token}s.
     */
    public Message tokenize(Message message) {
        message.setTokens(getTokens(message));
        return message;
    }

}
