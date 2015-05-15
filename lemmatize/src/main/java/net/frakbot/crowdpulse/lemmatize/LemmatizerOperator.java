package net.frakbot.crowdpulse.lemmatize;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public class LemmatizerOperator implements Observable.Operator<Message, Message> {
    private final ILemmatizer lemmatizer;

    public LemmatizerOperator(ILemmatizer lemmatizer) {
        this.lemmatizer = lemmatizer;
    }
    
    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = lemmatizer.lemmatizeMessage(message);
                subscriber.onNext(message);
            }
        };
    }
}
