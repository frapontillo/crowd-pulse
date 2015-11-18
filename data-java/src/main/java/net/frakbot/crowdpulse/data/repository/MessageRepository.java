/*
 * Copyright 2015 Francesco Pontillo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.frakbot.crowdpulse.data.repository;

import net.frakbot.crowdpulse.data.entity.Message;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import rx.Observable;

import java.util.Date;

/**
 * {@link Repository} for {@link Message}s.
 *
 * @author Francesco Pontillo
 */
public class MessageRepository extends Repository<Message, ObjectId> {

    public MessageRepository() {
        super();
    }

    public MessageRepository(String db) {
        super(db);
    }

    /**
     * Get a Message by checking its original source ID.
     *
     * @param originalId The original ID of the message at the source.
     * @return The found {@link Message} or {@code null}.
     */
    public Message getByOriginalId(String originalId) {
        Query<Message> query = createQuery();
        query.field("oId").equal(originalId);
        return query.get();
    }

    @Override
    public Query<Message> findBetweenKeys(ObjectId from, ObjectId to) {
        return super.findBetweenKeys(from, to).order("date");
    }

    public Observable<Message> findBetweenDates(Date since, Date until) {
        Query<Message> query = createQuery();
        if (since != null) {
            query.field("date").greaterThanOrEq(since);
        }
        if (until != null) {
            query.field("date").lessThanOrEq(until);
        }
        query.order("date");
        return Observable.from(query.fetch());
    }
}
