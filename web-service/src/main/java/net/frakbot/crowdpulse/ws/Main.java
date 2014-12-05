/*
 * Copyright 2014 Francesco Pontillo
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

package net.frakbot.crowdpulse.ws;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "/sentinel-ws/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer(URI serverURI) {
        // create a resource config that scans for JAX-RS resources and providers
        // in net.frakbot.socialsentinel.ws package
        final ResourceConfig rc = new ResourceConfig().packages("net.frakbot.socialsentinel.ws");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(serverURI, rc);
    }

    /**
     * Builds a {link java.net.URI} starting from scheme, host name and host port. The path is always sentinel-ws.
     *
     * @param scheme    The scheme (protocol).
     * @param hostName  The name of the host, defaults to the default network host.
     * @param hostPort  The port number, default to the default HTTP port.
     *
     * @return {@link java.net.URI} that can be used to start a Grizzly HTTP server.
     *
     * @throws URISyntaxException Thrown if the {@link java.net.URI} can't be built.
     */
    public static URI buildURI(String scheme, String hostName, int hostPort) throws URISyntaxException {
        return new URI(scheme, null, hostName, hostPort, BASE_URI, null, null);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        URI serverURI = buildURI(null, "0.0.0.0", 8080);
        final HttpServer server = startServer(serverURI);
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}

