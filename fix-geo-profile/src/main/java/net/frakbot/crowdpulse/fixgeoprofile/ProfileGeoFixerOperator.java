package net.frakbot.crowdpulse.fixgeoprofile;

import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.data.entity.Profile;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Francesco Pontillo
 */
public class ProfileGeoFixerOperator implements Observable.Operator<Profile, Profile> {
    private final IProfileGeoFixer fixer;

    public ProfileGeoFixerOperator(IProfileGeoFixer fixer) {
        this.fixer = fixer;
    }

    @Override
    public Subscriber<? super Profile> call(Subscriber<? super Profile> subscriber) {
        return new CrowdSubscriber<Profile>(subscriber) {
            @Override
            public void onNext(Profile profile) {
                profile = fixer.geoFixProfile(profile);
                subscriber.onNext(profile);
            }
        };
    }
}
