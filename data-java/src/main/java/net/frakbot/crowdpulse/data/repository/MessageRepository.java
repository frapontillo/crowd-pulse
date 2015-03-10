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
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.CriteriaContainer;
import org.mongodb.morphia.query.FieldCriteria;
import org.mongodb.morphia.query.Query;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class MessageRepository extends Repository<Message, ObjectId> {

    public List<Message> getGeoConsolidationCandidates(String fromId, String toId) {
        Query<Message> query = findBetweenIds(fromId, toId);
        query.or(
                query.criteria("latitude").doesNotExist().criteria("longitude").doesNotExist(),
                query.criteria("latitude").equal(0).criteria("longitude").equal(0)
        );
        return query.asList();
    }

    public List<Message> getLanguageDetectionCandidates(String fromId, String toId) {
        Query<Message> query = findBetweenIds(fromId, toId);
        query.or(
                query.criteria("language").doesNotExist(),
                query.criteria("language").equal("")
        );
        return query.asList();
    }

}
