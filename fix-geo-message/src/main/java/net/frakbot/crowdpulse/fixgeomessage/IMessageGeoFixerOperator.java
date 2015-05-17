package net.frakbot.crowdpulse.fixgeomessage;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public abstract class IMessageGeoFixerOperator implements Observable.Operator<Message, Message> {

    @Override
    public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new CrowdSubscriber<Message>(subscriber) {
            @Override
            public void onNext(Message message) {
                message = geoFixMessage(message);
                subscriber.onNext(message);
            }
        };
    }

    protected Message geoFixMessage(Message message) {
        Double[] coordinates = getCoordinates(message);
        if (coordinates != null && coordinates.length == 2) {
            message.setLatitude(coordinates[0]);
            message.setLongitude(coordinates[1]);
        }
        return message;
    }

    /**
     * Actual coordinates geo-fixer for the input {@link Message}.
     *
     * @param message The {@link Message} to retrieve geo-coordinates for.
     * @return An array of {@link Double} representing, in order, latitude and longitude.
     */
    public abstract Double[] getCoordinates(Message message);

}
