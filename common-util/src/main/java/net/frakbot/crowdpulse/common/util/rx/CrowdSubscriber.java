package net.frakbot.crowdpulse.common.util.rx;

import rx.Subscriber;
import rx.observers.SafeSubscriber;

/**
 * Generic stream {@link SafeSubscriber} that can be used for most tasks
 * inside Crowd Pulse.
 *
 * Default actions:
 * <ul>
 *     <li>when completed, notifies the completion downstream</li>
 *     <li>in case of an error, it prints the stacktrace and notifies the error</li>
 *     <li>for each item needing processing, calls a custom function that has to be implemented</li>
 * </ul>
 *
 * TODO: this {@link Subscriber} will also implement some custom statistic logging.
 *
 * @author Francesco Pontillo
 */
public abstract class CrowdSubscriber<T> extends SafeSubscriber<T> {

    public CrowdSubscriber(Subscriber<? super T> actual) {
        super(
                new Subscriber<T>() {
                    @Override
                    public void onCompleted() {
                        // TODO: add some statistics about the total task processing time
                        actual.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        // TODO: should we stop the stream if an error occurs?
                        actual.onError(e);
                    }

                    @Override
                    public void onNext(T t) {
                        // TODO: add some statistics about processing time for each item
                        onNext(t);
                    }
                });
    }

    public abstract void onNext(T t);
}

