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

import net.frakbot.crowdpulse.data.entity.Tag;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

/**
 * @author Francesco Pontillo
 */
public class TagRepository extends Repository<Tag, String> {

    /**
     * Insert or update a tag by matching its _id and eventually saving it for the first time or updating its
     * sources list.
     * @param tag The {@link Tag} to be inserted or updated
     * @return The updated {@link Tag}
     */
    public Tag insertOrUpdate(Tag tag) {
        Query<Tag> query = createQuery().field("_id").equal(tag.getId());
        Tag originalTag = findOne(query);

        // if the tag was already inserted, update its sources
        if (originalTag != null) {
            UpdateOperations<Tag> updateTag = createUpdateOperations().addAll("sources", tag.getSources(), false);
            UpdateResults res = updateFirst(query, updateTag);
            return findOne(query);
        }

        // if the tag is new, add it
        save(tag);
        return tag;
    }
}
