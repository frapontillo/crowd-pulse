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

package net.frakbot.crowdpulse.core.cli;

import com.beust.jcommander.JCommander;
import com.google.gson.JsonObject;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.DateUtil;
import net.frakbot.crowdpulse.common.util.FileUtil;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import net.frakbot.crowdpulse.core.graph.Graph;
import net.frakbot.crowdpulse.core.graph.GraphUtil;
import net.frakbot.crowdpulse.core.graph.Node;
import net.frakbot.crowdpulse.core.plugin.ProjectRunEndPlugin;
import net.frakbot.crowdpulse.core.plugin.ProjectRunStartPlugin;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dynamic pipeline builder from a JSON configuration file.
 * <p>
 * Because Blade Runner. Get it? HA!
 *
 * @author Francesco Pontillo
 */
public class Blade {
    private final static Logger logger = CrowdLogger.getLogger(Blade.class);

    private HashMap<String, Observable> observableMap;
    private List<Observable> terminalObservables;

    public static void main(String args[]) throws ClassNotFoundException, FileNotFoundException {
        Blade runner = new Blade(args);
        runner.run();
    }

    public Blade(String args[]) throws ClassNotFoundException, FileNotFoundException {
        BladeParameters parameters = new BladeParameters();
        new JCommander(parameters, args);

        // get the appropriate input stream (file or stdin)
        InputStream inputStream;
        if (parameters.hasFile()) {
            inputStream = FileUtil.readFileFromPathOrResource(parameters.getFile(), Blade.class);
            logger.debug("Reading configuration from file...");
        } else {
            inputStream = System.in;
            logger.debug("Reading configuration from standard input...");
        }

        // read line by line from the input stream
        Scanner scanner = new Scanner(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        logger.debug("Configuration read.");

        String config = sb.toString();
        Graph graph = GraphUtil.readGraphFromString(config);
        logger.debug("Graph built.");

        // if the parameters are appropriate, wrap the graph with a fixed root and terminal node
        wrapGraphMaybe(graph, parameters);

        observableMap = new HashMap<>(graph.getNodes().size());
        terminalObservables = new ArrayList<>();

        // start from the root nodes and transform the Graph into Observables
        List<Node> rootNodes = graph.getRoots();

        logger.debug("Building Observables...");
        buildObservables(graph, rootNodes);
        logger.debug("Observables built.");
    }

    /**
     * Wrap the given {@link Graph} between two new steps:
     * <ul>
     * <li>the first one sets the starting date, the process ID and the log path into the project run</li>
     * <li>the last one sets the ending date into the project run</li>
     * </ul>
     *
     * @param graph      The {@link Graph} to wrap.
     * @param parameters The {@link BladeParameters} to use (they can specify the log, db and project run).
     */
    private void wrapGraphMaybe(Graph graph, BladeParameters parameters) {
        if (parameters.mustSetProjectRun()) {
            logger.debug("Using project run information from CLI parameters, wrapping graph...");

            String uid = DateUtil.toISOString(new Date());
            JsonObject config = new JsonObject();
            config.addProperty("projectRunId", parameters.getRun());
            config.addProperty("log", parameters.getLog());
            config.addProperty("db", parameters.getDb());

            Node first = new Node();
            first.setGraph(graph);
            first.setName("wrap_start_" + uid);
            first.setPlugin(ProjectRunStartPlugin.PLUGIN_NAME);
            first.setConfig(config);

            Node last = new Node();
            last.setGraph(graph);
            last.setName("wrap_end_" + uid);
            last.setPlugin(ProjectRunEndPlugin.PLUGIN_NAME);
            last.setConfig(config);

            graph.prependSingleRoot(first);
            graph.appendSingleTerminal(last);

            logger.debug("Graph wrapped with info from CLI parameters.");
        }
    }

    /**
     * Merges all of the terminal {@link Observable}s, publishes them, subscribes to them and eventually connect to
     * them.
     *
     * @throws ClassNotFoundException If an {@link IPlugin} instance could not be found.
     */
    public void run() throws ClassNotFoundException {
        ConnectableObservable stream = mergeObservables(terminalObservables).publish();

        SubscriptionGroupLatch allSubscriptions = new SubscriptionGroupLatch(1);

        // subscribe to the connectable stream
        Subscription subscription = stream.subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                logger.debug("EXECUTION: COMPLETED");
                allSubscriptions.countDown();
            }

            @Override
            public void onError(Throwable e) {
                logger.error("EXECUTION: ERRORED");
                allSubscriptions.countDown();
            }

            @Override
            public void onNext(Object o) {
                logger.debug(o.toString());
            }
        });

        allSubscriptions.setSubscriptions(subscription);

        stream.connect();
        logger.info("Starting process...");

        allSubscriptions.waitAllUnsubscribed();
        logger.info("Process completed.");
    }

    /**
     * Build all of the {@link Observable}s referenced by list of {@link Node}s.
     *
     * @param graph The {@link Graph} that holds the nodes.
     * @param nodes The {@link List} of {@link Node}s to build observables for.
     * @throws ClassNotFoundException If an {@link IPlugin} instance could not be found.
     */
    private void buildObservables(Graph graph, List<Node> nodes) throws ClassNotFoundException {
        for (Node node : nodes) {
            buildObservable(graph, node);
        }
    }

    /**
     * Build an {@link Observable} for a {@link Node} in a given {@link Graph}.
     *
     * @param graph The {@link Graph} that holds the {@link Node}.
     * @param node  The {@link Node} to build the {@link Observable} for.
     * @throws ClassNotFoundException If an {@link IPlugin} instance could not be found.
     */
    private void buildObservable(Graph graph, Node node) throws ClassNotFoundException {
        // if the observableMap already has an Observable for the current Node, it was already built before
        if (observableMap.get(node.getName()) != null) {
            return;
        }
        logger.debug("Building Observable {}.", node.getName());
        // if the node has previous nodes bound to it
        List<Node> previousNodes = node.getPrev();
        List<Observable> previousObservables = null;
        if (node.hasPrev()) {
            // build the previous nodes if they're not already built
            buildObservables(graph, previousNodes);
            // get all the previous Observables from the observableMap
            previousObservables = previousNodes.stream()
                    .map(prevNode -> observableMap.get(prevNode.getName()))
                    .collect(Collectors.toList());
        }
        // at this point we know for sure that the previous nodes of node are already built and in the observableMap

        // the previous observable creation may have already created the observable we're trying to build: skip it!
        if (observableMap.get(node.getName()) != null) {
            return;
        }

        IPlugin plugin = PluginProvider.getPlugin(node.getPlugin());
        plugin.setProcessInfo(graph.getProcess());
        plugin.setJobName(node.getName());
        // use the previous observables in the plugin
        Observable observable = plugin.process(node.getConfig(), previousObservables);

        // if the node has n > 1 exit points, cache the emitted values so not to repeat its execution n - 1 more times
        if (node.getNext() != null && node.getNext().size() > 1) {
            observable = observable.cache();
        }

        // save the new observable into the map
        observableMap.put(node.getName(), observable);

        // if the node has no next nodes, save it among the terminal nodes so to subscribe on it
        if (!node.hasNext()) {
            terminalObservables.add(observable);
        } else {
            buildObservables(graph, node.getNext());
        }
    }

    private Observable mergeObservables(List<Observable> observableList) {
        Observable[] observables = observableList.toArray(new Observable[]{});
        return Observable.merge(observables);
    }
}
