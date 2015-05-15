package net.frakbot.crowdpulse.tag;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public class TaggerOperator implements Observable.Operator<Message, Message> {
    private final ITagger tagger;

    public TaggerOperator(ITagger tagger) {
        this.tagger = tagger;
    }

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = tagger.tagMessage(message);
                subscriber.onNext(message);
            }
        };
    }
}
