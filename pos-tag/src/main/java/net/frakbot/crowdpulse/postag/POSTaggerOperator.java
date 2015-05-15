package net.frakbot.crowdpulse.postag;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public class POSTaggerOperator implements Observable.Operator<Message, Message> {
    private final IPOSTagger tagger;

    public POSTaggerOperator(IPOSTagger tagger) {
        this.tagger = tagger;
    }
    
    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = tagger.posTagMessage(message);
                subscriber.onNext(message);
            }
        };
    }
}
