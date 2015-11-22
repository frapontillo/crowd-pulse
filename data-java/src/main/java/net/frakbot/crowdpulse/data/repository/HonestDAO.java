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

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.DatastoreImpl;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.DAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 * @author Francesco Pontillo
 */
@SuppressWarnings({"unchecked", "deprecation"})
public class HonestDAO<T, K> implements DAO<T, K> {
    //CHECKSTYLE:OFF
    /**
     * @deprecated please use the getter for this field
     */
    protected Class<T> entityClazz;
    /**
     * @deprecated please use the getter for this field
     */
    protected DatastoreImpl ds;
    //CHECKSTYLE:ON

    protected void initDAO(final Datastore ds) {
        this.ds = (DatastoreImpl) ds;
        initType(((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]));
    }

    protected void initType(final Class<T> type) {
        entityClazz = type;
        ds.getMapper().addMappedClass(type);
    }

    protected void initDS(final MongoClient mongoClient, final Morphia mor, final String db) {
        ds = new DatastoreImpl(mor, mongoClient, db);
    }

    public DatastoreImpl getDs() {
        return ds;
    }

    public Class<T> getEntityClazz() {
        return entityClazz;
    }

    /**
     * Converts from a List<Key> to their id values
     */
    protected List<?> keysToIds(final List<Key<T>> keys) {
        final List<Object> ids = new ArrayList<Object>(keys.size() * 2);
        for (final Key<T> key : keys) {
            ids.add(key.getId());
        }
        return ids;
    }

    /**
     * The underlying collection for this DAO
     */
    public DBCollection getCollection() {
        return ds.getCollection(entityClazz);
    }

    public Query<T> createQuery() {
        return ds.createQuery(entityClazz);
    }

    public UpdateOperations<T> createUpdateOperations() {
        return ds.createUpdateOperations(entityClazz);
    }

    public Class<T> getEntityClass() {
        return entityClazz;
    }

    public Key<T> save(final T entity) {
        return ds.save(entity);
    }

    public Key<T> save(final T entity, final WriteConcern wc) {
        return ds.save(entity, wc);
    }

    public UpdateResults updateFirst(final Query<T> q, final UpdateOperations<T> ops) {
        return ds.updateFirst(q, ops);
    }

    public UpdateResults update(final Query<T> q, final UpdateOperations<T> ops) {
        return ds.update(q, ops);
    }

    public WriteResult delete(final T entity) {
        return ds.delete(entity);
    }

    public WriteResult delete(final T entity, final WriteConcern wc) {
        return ds.delete(entity, wc);
    }

    public WriteResult deleteById(final K id) {
        return ds.delete(entityClazz, id);
    }

    public WriteResult deleteByQuery(final Query<T> q) {
        return ds.delete(q);
    }

    public T get(final K id) {
        return ds.get(entityClazz, id);
    }

    @SuppressWarnings("unchecked")
    public List<K> findIds() {
        return (List<K>) keysToIds(ds.find(entityClazz).asKeyList());
    }

    @SuppressWarnings("unchecked")
    public List<K> findIds(final Query<T> q) {
        return (List<K>) keysToIds(q.asKeyList());
    }

    @SuppressWarnings("unchecked")
    public List<K> findIds(final String key, final Object value) {
        return (List<K>) keysToIds(ds.find(entityClazz, key, value).asKeyList());
    }

    public Key<T> findOneId() {
        return findOneId(ds.find(entityClazz));
    }

    public Key<T> findOneId(final String key, final Object value) {
        return findOneId(ds.find(entityClazz, key, value));
    }

    public Key<T> findOneId(final Query<T> query) {
        Iterator<Key<T>> keys = query.fetchKeys().iterator();
        return keys.hasNext() ? keys.next() : null;
    }

    public boolean exists(final String key, final Object value) {
        return exists(ds.find(entityClazz, key, value));
    }

    public boolean exists(final Query<T> q) {
        return ds.getCount(q) > 0;
    }

    public long count() {
        return ds.getCount(entityClazz);
    }

    public long count(final String key, final Object value) {
        return count(ds.find(entityClazz, key, value));
    }

    public long count(final Query<T> q) {
        return ds.getCount(q);
    }

    public T findOne(final String key, final Object value) {
        return ds.find(entityClazz, key, value).get();
    }

    /* (non-Javadoc)
     * @see org.mongodb.morphia.DAO#findOne(org.mongodb.morphia.query.Query)
     */
    public T findOne(final Query<T> q) {
        return q.get();
    }

    /* (non-Javadoc)
     * @see org.mongodb.morphia.DAO#find()
     */
    public QueryResults<T> find() {
        return createQuery();
    }

    /* (non-Javadoc)
     * @see org.mongodb.morphia.DAO#find(org.mongodb.morphia.query.Query)
     */
    public QueryResults<T> find(final Query<T> q) {
        return q;
    }

    /* (non-Javadoc)
     * @see org.mongodb.morphia.DAO#getDatastore()
     */
    public Datastore getDatastore() {
        return ds;
    }

    public void ensureIndexes() {
        ds.ensureIndexes(entityClazz);
    }

}
