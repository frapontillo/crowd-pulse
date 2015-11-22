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

import com.mongodb.MongoClient;
import net.frakbot.crowdpulse.data.entity.Message;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Data Access Layer to a database, all of the connection parameters must be specified in a {@code database.properties}
 * file (see the constructor {@link DataLayer#DataLayer(String)}).
 *
 * @author Francesco Pontillo
 */
public class DataLayer {
    private Datastore datastore;

    /**
     * Reads from a {@code database.properties} file and sets up the DB client and connection,
     * mapping all of the entity classes.
     *
     * You can optionally specify a target DB name that will override the one in the properties file.
     *
     * The configuration file must have the following:
     * <ul>
     * <li>{@code database.host}, the server host of the DB instance</li>
     * <li>{@code database.port}, the server port of the DB instance</li>
     * <li>{@code database.name}, the name of the DB collection</li>
     * <li>{@code database.username}, the username with access to the collection</li>
     * <li>{@code database.password}, the password of the user with access to the collection</li>
     * </ul>
     *
     * @param db The target database to connect to.
     */
    public DataLayer(String db) {
        // the input target overrides the DB name in the `database.properties` file
        DBConfig config = new DBConfig(getClass(), db);

        MongoClient client = new MongoClient(config.getServerAddress(), config.getCredentials());

        // map all Morphia classes
        Morphia morphia = new Morphia();
        morphia.mapPackageFromClass(Message.class);
        // create and/or get the datastore
        datastore = morphia.createDatastore(client, config.getDBName());
    }

    /**
     * Returns the available instance of the datastore.
     *
     * @return The {@link org.mongodb.morphia.Datastore} instance associated with the DataLayer.
     */
    public Datastore getDatastore() {
        return datastore;
    }
}
