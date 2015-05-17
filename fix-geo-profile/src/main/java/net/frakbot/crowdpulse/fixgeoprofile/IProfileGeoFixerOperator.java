package net.frakbot.crowdpulse.fixgeoprofile;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Profile;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public abstract class IProfileGeoFixerOperator implements Observable.Operator<Profile, Profile> {

    @Override
    public Subscriber<? super Profile> call(Subscriber<? super Profile> subscriber) {
        return new CrowdSubscriber<Profile>(subscriber) {
            @Override
            public void onNext(Profile profile) {
                profile = geoFixProfile(profile);
                subscriber.onNext(profile);
            }
        };
    }

    protected Profile geoFixProfile(Profile profile) {
        Double[] coordinates = getCoordinates(profile);
        if (coordinates != null && coordinates.length == 2) {
            profile.setLatitude(coordinates[0]);
            profile.setLongitude(coordinates[1]);
        }
        return profile;
    }

    /**
     * Actual retrieval of {@link Profile} coordinates happens here.
     * @param profile The {@link Profile} to retrieve coordinates for.
     * @return Array of {@link Double} containing, in order, latitude and longitude.
     */
    public abstract Double[] getCoordinates(Profile profile);

}
