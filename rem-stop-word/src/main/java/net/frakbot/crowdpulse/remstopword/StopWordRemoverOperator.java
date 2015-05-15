package net.frakbot.crowdpulse.remstopword;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public class StopWordRemoverOperator implements Observable.Operator<Message, Message> {
    private final IStopWordRemover remover;

    public StopWordRemoverOperator(IStopWordRemover remover) {
        this.remover = remover;
    }

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = remover.stopWordRemoveMessage(message);
                subscriber.onNext(message);
            }
        };
    }
}
