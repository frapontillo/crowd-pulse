package net.frakbot.crowdpulse.detectlanguage;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * Rx {@link rx.Observable.Operator} for categorizing the tags inside a {@link Message}.
 *
 * @author Francesco Pontillo
 */
public abstract class ILanguageDetectorOperator implements Observable.Operator<Message, Message> {

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message.setLanguage(getLanguage(message));
                subscriber.onNext(message);
            }
        };
    }

    /**
     * Actual language retrieval for the given {@link Message}.
     *
     * @param message The {@link Message} to process.
     * @return A ISO-compliant {@link String} representing the most probable language for the {@link Message}.
     */
    public abstract String getLanguage(Message message);

}
