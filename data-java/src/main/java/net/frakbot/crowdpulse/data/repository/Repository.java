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
 * @author Francesco Pontillo
 */
public class Repository<T,K> extends BasicDAO<T,K> {
    protected Repository() {
        super(DataLayer.getDataLayer().getDatastore());
    }

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

    public Observable<T> get() {
        return Observable.from(findBetweenKeys(null, null).fetch());
    }

    public List<T> getBetweenKeys(K from, K to) {
        return findBetweenKeys(from, to).asList();
    }
}
