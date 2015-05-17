package net.frakbot.crowdpulse.lemmatize;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class ILemmatizerOperator implements Observable.Operator<Message, Message> {

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = lemmatizeMessage(message);
                subscriber.onNext(message);
            }
        };
    }

    /**
     * Lemmatize all the {@link Token}s of the {@link Message}, then return the input {@link Message}.
     *
     * @param message The {@link Message} to process.
     * @return the {@link Message} with lemmatized {@link Token}s.
     */
    public Message lemmatizeMessage(Message message) {
        message.setTokens(lemmatizeMessageTokens(message));
        return message;
    }

    /**
     * Lemmatize the given {@link Token}s by setting the {@link Token#lemma} property.
     * @param message The {@link Message} whose {@link List} of {@link Token}s will be modified in order to include the
     *                (optional) lemma.
     * @return The same {@link List} of {@link Token}s of the input {@link Message}, eventually modified.
     */
    public abstract List<Token> lemmatizeMessageTokens(Message message);

}
