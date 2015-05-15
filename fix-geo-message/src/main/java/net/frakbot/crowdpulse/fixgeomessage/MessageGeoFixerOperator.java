package net.frakbot.crowdpulse.fixgeomessage;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public class MessageGeoFixerOperator implements Observable.Operator<Message, Message> {
    private final IMessageGeoFixer fixer;

    public MessageGeoFixerOperator(IMessageGeoFixer fixer) {
        this.fixer = fixer;
    }

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = fixer.geoFixMessage(message);
                subscriber.onNext(message);
            }
        };
    }
}
