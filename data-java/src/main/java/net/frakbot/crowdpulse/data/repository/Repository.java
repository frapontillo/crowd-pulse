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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import net.frakbot.crowdpulse.data.entity.Message;
import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import rx.Observable;

import java.util.*;

/**
 * Generic repository for MongoDB collections, where:
 * <ul>
 * <li>{@link T} is the class of stored objects</li>
 * <li>{@link K} is the class of the object IDs</li>
 * </ul>
 *
 * @author Francesco Pontillo
 */
public abstract class Repository<T, K> extends HonestDAO<T,K> {
    private MongoCollection<Document> collection;
    Morphia morphia;
    private Datastore datastore;
    private MongoDatabase rxDatastore;

    /**
     * Create a new Repository using the default configuration in `database.properties`.
     */
    protected Repository() {
        this(null);
    }

    /**
     * Create a new Repository using the default configuration in `database.properties` and overriding the db name
     * with the one in input.
     * @param db The database name to use for this Repository instance.
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public Repository(String db) {
        DBConfig config = new DBConfig(getClass(), db);

        MongoClient client = new MongoClient(config.getServerAddress(), config.getCredentials());

        // map all Morphia classes
        morphia = new Morphia();
        morphia.mapPackageFromClass(Message.class);

        ClusterSettings clusterSettings = ClusterSettings.builder()
                .hosts(Collections.singletonList(config.getServerAddress())).build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .clusterSettings(clusterSettings)
                .credentialList(config.getCredentials())
                .build();
        com.mongodb.reactivestreams.client.MongoClient rxClient = MongoClients.create(settings);

        // create and/or get the datastore
        datastore = morphia.createDatastore(client, config.getDBName());
        // init the DAO
        initDAO(datastore);
        ensureIndexes();

        // create the reactive database
        rxDatastore = rxClient.getDatabase(config.getDBName());

    }

    public abstract String getCollectionName();

    public MongoCollection<Document> getRxCollection() {
        if (collection == null) {
            collection = rxDatastore.getCollection(getCollectionName());
        }
        return collection;
    }

    public DBObject documentToDBObject(Document document) {
        Set<Map.Entry<String, Object>> entrySet = document.entrySet();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(entrySet.size());
        entrySet.forEach(e -> map.put(e.getKey(), e.getValue()));
        DBObject dbObject = new BasicDBObject(map);
        return dbObject;
    }

    /**
     * Find an element by its ID.
     *
     * @param id The ID of the element to find.
     * @return The found element or {@code null}.
     */
    public T findById(K id) {
        Query<T> query = createQuery().field("_id").equal(id);
        return findOne(query);
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
