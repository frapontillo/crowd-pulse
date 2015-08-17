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

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.rx.SubscriptionGroupLatch;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.PluginProvider;
import net.frakbot.crowdpulse.core.graph.Graph;
import net.frakbot.crowdpulse.core.graph.GraphUtil;
import net.frakbot.crowdpulse.core.graph.Node;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static void main(String args[]) throws ClassNotFoundException {
        Blade runner = new Blade(args);
        runner.run();
    }

    public Blade(String args[]) throws ClassNotFoundException {
        String configFile = "config.json";
        if (args != null && args.length > 0) {
            configFile = args[0];
        }
        Graph graph = GraphUtil.readGraph(configFile, Blade.class);

        observableMap = new HashMap<>(graph.getNodes().size());
        terminalObservables = new ArrayList<>();

        // start from the root nodes and transform the Graph into Observables
        List<Node> rootNodes = graph.getRoots();
        buildObservables(graph, rootNodes);
    }

    /**
     * Merges all of the terminal {@link Observable}s, publishes them, subscribes to them and eventually connect to
     * them.
     *
     * @throws ClassNotFoundException If an {@link IPlugin} instance could not be found.
     * @throws FileNotFoundException
     */
    public void run() throws ClassNotFoundException {
        ConnectableObservable stream = mergeObservables(terminalObservables).publish();

        SubscriptionGroupLatch allSubscriptions = new SubscriptionGroupLatch(1);

        // subscribe to the connectable stream
        Subscription subscription = stream.subscribe(new Subscriber<Object>() {
            @Override public void onCompleted() {
                logger.debug("EXECUTION: COMPLETED");
                allSubscriptions.countDown();
            }

            @Override public void onError(Throwable e) {
                logger.error("EXECUTION: ERRORED");
                allSubscriptions.countDown();
            }

            @Override public void onNext(Object o) {
                logger.debug(o.toString());
            }
        });

        allSubscriptions.setSubscriptions(subscription);
        stream.connect();
        logger.info("Connected.");

        allSubscriptions.waitAllUnsubscribed();
        logger.info("Done.");
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
        // at this point we know for sure that the previous nodes of node are already built and in the observableMapx

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
