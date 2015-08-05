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

import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import rx.Observable;

import java.util.List;

/**
 * Generic repository for MongoDB collections, where:
 * <ul>
 * <li>{@link T} is the class of stored objects</li>
 * <li>{@link K} is the class of the object IDs</li>
 * </ul>
 *
 * @author Francesco Pontillo
 */
public class Repository<T, K> extends BasicDAO<T, K> {

    /**
     * Create a new Repository using the default configuration in `database.properties`.
     */
    protected Repository() {
        super(DataLayer.getDataLayer(null).getDatastore());
    }

    /**
     * Create a new Repository using the default configuration in `database.properties` and overriding the db name
     * with the one in input.
     * @param db The database name to use for this Repository instance.
     */
    public Repository(String db) {
        super(DataLayer.getDataLayer(db).getDatastore());
    }

    /**
     * Create a {@link Query} to retrieve elements between two {@link K} representations of object IDs.
     *
     * @param from The ID to start retrieve elements from.
     * @param to   The Id to stop retrieve elements at.
     * @return A {@link Query} to retrieve elements between two IDs.
     */
    public Query<T> findBetweenKeys(K from, K to) {
        Query<T> query = createQuery();
        if (from != null) {
            query.field("_id").greaterThanOrEq(from);
        }
        if (to != null) {
            query.field("_id").lessThanOrEq(to);
        }
        return query;
    }

    /**
     * Get all the elements of the collection as an {@link Observable}<{@link T}>.
     *
     * @return {@link Observable}<{@link T}> that will emit all of the objects in the Collection.
     */
    public Observable<T> get() {
        return Observable.from(findBetweenKeys(null, null).fetch());
    }

    /**
     * Get the elements of the collection with IDs between {@code from} and {@code to} as a {@link List}<{@link T}>.
     *
     * @param from The ID to start retrieve elements from.
     * @param to   The Id to stop retrieve elements at.
     * @return {@link List}<{@link T}> that will contain all of the objects in the Collection.
     */
    public List<T> getBetweenKeys(K from, K to) {
        return findBetweenKeys(from, to).asList();
    }
}
