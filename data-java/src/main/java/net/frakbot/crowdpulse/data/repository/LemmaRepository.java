package net.frakbot.crowdpulse.data.repository;

import net.frakbot.crowdpulse.data.entity.Lemma;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class LemmaRepository extends Repository<Lemma, ObjectId> {

    public Lemma getLemma(String postag, String token) {
        Query<Lemma> query = createQuery();
        query.and(
                query.criteria("posTag").equal(postag),
                query.criteria("token").equal(token)
        );
        return query.get();
    }
}
