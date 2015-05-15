package net.frakbot.crowdpulse.tokenize;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public class TokenizerOperator implements Observable.Operator<Message, Message> {
    private final ITokenizer tokenizer;

    public TokenizerOperator(ITokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = tokenizer.tokenize(message);
                subscriber.onNext(message);
            }
        };
    }
}
