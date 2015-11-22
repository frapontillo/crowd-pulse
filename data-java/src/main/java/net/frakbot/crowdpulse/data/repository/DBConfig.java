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

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import net.frakbot.crowdpulse.common.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Configuration holder for database connections.
 *
 * @author Francesco Pontillo
 */
public class DBConfig {

    private static final String PROP_DATABASE_HOST = "database.host";
    private static final String PROP_DATABASE_PORT = "database.port";
    private static final String PROP_DATABASE_NAME = "database.name";
    private static final String PROP_DATABASE_USERNAME = "database.username";
    private static final String PROP_DATABASE_PASSWORD = "database.password";

    private String host;
    private int port;
    private String dbName;
    private String username;
    private String password;

    /**
     * Read the database configuration from a {@code database.properties} file in the
     * class loader of {@code clazz}.
     *
     * @param clazz The class with a {@code database.properties} file in its resources.
     */
    public DBConfig(Class clazz) {
        InputStream configInput = clazz.getClassLoader().getResourceAsStream("database.properties");
        Properties prop = new Properties();
        try {
            prop.load(configInput);
        } catch (IOException noFileException) {
            noFileException.printStackTrace();
            return;
        }
        setHost(prop.getProperty(PROP_DATABASE_HOST));
        setPort(Integer.parseInt(prop.getProperty(PROP_DATABASE_PORT)));
        setDBName(prop.getProperty(PROP_DATABASE_NAME));
        setUsername(prop.getProperty(PROP_DATABASE_USERNAME));
        setPassword(prop.getProperty(PROP_DATABASE_PASSWORD));
    }

    /**
     * Build the object as in {@link DBConfig#DBConfig(Class)} but overwrites the database name
     * if it is defined and not empty.
     *
     * @param clazz The class with a {@code database.properties} file in its resources.
     * @param db The database name to set in the config object.
     */
    public DBConfig(Class clazz, String db) {
        this(clazz);
        if (!StringUtil.isNullOrEmpty(db)) {
            setDBName(db);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDBName() {
        return dbName;
    }

    public void setDBName(String dbName) {
        this.dbName = dbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ServerAddress getServerAddress() {
        return new ServerAddress(getHost(), getPort());
    }

    public List<MongoCredential> getCredentials() {
        List<MongoCredential> credentialList = new ArrayList<>(1);
        if (!StringUtil.isNullOrEmpty(getUsername()) && !StringUtil.isNullOrEmpty(getPassword())) {
            credentialList = Collections.singletonList(MongoCredential.createMongoCRCredential(
                    getUsername(), getDBName(), getPassword().toCharArray()));
        }
        return credentialList;
    }
}
