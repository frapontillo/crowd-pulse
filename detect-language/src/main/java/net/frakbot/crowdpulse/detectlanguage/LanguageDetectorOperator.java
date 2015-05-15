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
public class LanguageDetectorOperator implements Observable.Operator<Message, Message> {
    private final ILanguageDetector detector;

    public LanguageDetectorOperator(ILanguageDetector detector) {
        this.detector = detector;
    }

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = detector.setLanguage(message);
                subscriber.onNext(message);
            }
        };
    }
}
