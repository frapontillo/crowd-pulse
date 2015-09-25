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
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import net.frakbot.crowdpulse.common.util.StringUtil;
import net.frakbot.crowdpulse.data.entity.Message;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Data Access Layer to a database, all of the connection parameters must be specified in a {@code database.properties}
 * file (see the constructor {@link DataLayer#DataLayer(String)}).
 * This class cannot be directly instantiated, please use {@link DataLayer#getDataLayer(String)}.
 *
 * @author Francesco Pontillo
 */
public class DataLayer {
    private static DataLayer dataLayer;
    private Datastore datastore;
    private MongoClient client;
    private Morphia morphia;

    private static final String PROP_DATABASE_HOST = "database.host";
    private static final String PROP_DATABASE_PORT = "database.port";
    private static final String PROP_DATABASE_NAME = "database.name";
    private static final String PROP_DATABASE_USERNAME = "database.username";
    private static final String PROP_DATABASE_PASSWORD = "database.password";

    /**
     * Get a singleton instance of DataLayer, by reading a {@code database.properties} file in the classpath.
     * The configuration file must have the following:
     * <ul>
     * <li>{@code database.host}, the server host of the DB instance</li>
     * <li>{@code database.port}, the server port of the DB instance</li>
     * <li>{@code database.name}, the name of the DB collection</li>
     * <li>{@code database.username}, the username with access to the collection</li>
     * <li>{@code database.password}, the password of the user with access to the collection</li>
     * </ul>
     *
     * @param db The DB name to get the Data Layer for.
     * @return A setup instance of DataLayer.
     */
    public static synchronized DataLayer getDataLayer(String db) {
        if (dataLayer == null) {
            dataLayer = new DataLayer(db);
        }
        return dataLayer;
    }

    /**
     * Private constructor.
     * Reads from a {@code database.properties} file and sets up the DB client and connection, mapping all of the
     * entity classes. You can optionally specify a target DB name that will override the one in the properties file.
     *
     * @param db The target database to connect to.
     */
    private DataLayer(String db) {
        InputStream configInput = getClass().getClassLoader().getResourceAsStream("database.properties");
        Properties prop = new Properties();
        try {
            prop.load(configInput);
        } catch (IOException noFileException) {
            System.err.println(noFileException);
        }
        String host = prop.getProperty(PROP_DATABASE_HOST);
        int port = Integer.parseInt(prop.getProperty(PROP_DATABASE_PORT));
        String dbName = prop.getProperty(PROP_DATABASE_NAME);
        String username = prop.getProperty(PROP_DATABASE_USERNAME);
        String password = prop.getProperty(PROP_DATABASE_PASSWORD);

        // the input target overrides the DB name in the `database.properties` file
        if (!StringUtil.isNullOrEmpty(db)) {
            dbName = db;
        }

        client = null;
        List<MongoCredential> credentialList = new ArrayList<MongoCredential>(1);
        if (username != null && !username.equals("") && password != null && !password.equals("")) {
            credentialList.add(MongoCredential.createMongoCRCredential(username, dbName, password.toCharArray()));
        }
        client = new MongoClient(new ServerAddress(host, port), credentialList);

        // map all Morphia classes
        morphia = new Morphia();
        morphia.mapPackageFromClass(Message.class);

        // create and/or get the datastore
        datastore = morphia.createDatastore(client, dbName);
    }

    /**
     * Returns the only available instance of the datastore.
     *
     * @return The {@link org.mongodb.morphia.Datastore} instance associated with the DataLayer.
     */
    public Datastore getDatastore() {
        return datastore;
    }
}
