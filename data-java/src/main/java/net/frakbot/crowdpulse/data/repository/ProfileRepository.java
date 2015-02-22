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

import net.frakbot.crowdpulse.data.entity.Profile;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class ProfileRepository extends Repository<Profile, ObjectId> {
    public List<Profile> getGeoConsolidationCandidates(String fromId, String toId) {
        Query<Profile> query = findBetweenIds(fromId, toId);
        query.field("latitude").doesNotExist();
        query.field("longitude").doesNotExist();
        query.field("location").exists();
        query.field("location").notEqual("");
        return query.asList();
    }
}
