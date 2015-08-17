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

package net.frakbot.crowdpulse.core.graph;

import net.frakbot.crowdpulse.common.util.spi.ProcessInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Directed graph implementation to load Crowd Pulse configurations from.
 *
 * @author Francesco Pontillo
 */
public class Graph {
    private ProcessInfo process;
    private List<Node> roots;
    private HashMap<String, Node> nodes;
    private HashMap<String, List<String>> edges;

    /**
     * Get the process info associated with the graph.
     *
     * @return The process info.
     */
    public ProcessInfo getProcess() {
        return process;
    }

    /**
     * Set the process info to associate with the graph.
     *
     * @param process The process info to associate with the graph.
     */
    public void setProcess(ProcessInfo process) {
        this.process = process;
    }

    /**
     * Get the root {@link Node}s of the Graph.
     *
     * @return The root {@link Node}s of the Graph.
     */
    public List<Node> getRoots() {
        return roots;
    }

    /**
     * Set the root {@link Node}s of the Graph.
     *
     * @param roots The root {@link Node}s of the Graph.
     */
    public void setRoots(List<Node> roots) {
        this.roots = roots;
    }

    /**
     * Get the {@link HashMap} of {@link Node}s, referenced by their name.
     *
     * @return {@link HashMap} of {@link Node}s.
     */
    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    /**
     * Set the {@link HashMap} of {@link Node}s, referenced by their name.
     *
     * @param nodes {@link HashMap} of {@link Node}s.
     */
    public void setNodes(HashMap<String, Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Get the list of edges in the Graph as a {@link HashMap}, where the key is the name of the {@link Node} where the
     * edge starts from and the value is a list of {@link Node} names where the edge ends.
     *
     * @return {@link HashMap} of edges.
     */
    public HashMap<String, List<String>> getEdges() {
        return edges;
    }

    /**
     * Set the list of edges in the Graph as a {@link HashMap}, where the key is the name of the {@link Node} where the
     * edge starts from and the value is a list of {@link Node} names where the edge ends.
     *
     * @param edges {@link HashMap} of edges.
     */
    public void setEdges(HashMap<String, List<String>> edges) {
        this.edges = edges;
    }

    /**
     * Get a {@link List} of {@link Node}s that aren't referenced by any other node in the directed Graph.
     *
     * @return {@link List} of root {@link Node}s for the Graph.
     */
    private List<Node> getRootNodes() {
        List<Node> rootNodes = new ArrayList<>();
        Set<String> referencesNodes = new HashSet<>();
        // build a set of referenced nodes
        for (String edgeKey : edges.keySet()) {
            referencesNodes.addAll(edges.get(edgeKey));
        }
        // add to rootNodes all the nodes not contained in the set of referenced nodes
        rootNodes.addAll(nodes.keySet().stream()
                .filter(nodeKey -> !referencesNodes.contains(nodeKey))
                .map(nodes::get)
                .collect(Collectors.toList()));
        return rootNodes;
    }

    /**
     * Build the Graph starting from the root {@link Node}s.
     *
     * @return The built Graph.
     */
    public Graph buildGraph() {
        // set the names to all of the nodes
        nodes.keySet().forEach(key -> nodes.get(key).setName(key));
        // get the root nodes and start setting the adjacent nodes in the graph (recursive function)
        roots = getRootNodes();
        roots.forEach(this::buildNode);
        return this;
    }

    /**
     * Recursively build a {@link Node} children in the Graph, one by one, by checking if they weren't already built.
     *
     * @param node The {@link Node} whose children will be used to expand this Graph.
     * @return The built input {@link Node}.
     */
    public Node buildNode(Node node) {
        // for every next node
        //  - add it to the list of adjacent nodes
        //  - add the current node to the list of its previous nodes
        //  - build the next node recursively
        List<String> nextNodeNames = edges.get(node.getName());
        if (nextNodeNames != null) {
            nextNodeNames.forEach(nextName -> {
                Node nextNode = nodes.get(nextName);
                nextNode.addPrev(node);
                node.addNext(nextNode);
                // avoid building the node if it was already processed
                if (!nextNode._wasBuilt) {
                    nextNode._wasBuilt = true;
                    buildNode(nextNode);
                }
            });
        }
        return node;
    }
}
