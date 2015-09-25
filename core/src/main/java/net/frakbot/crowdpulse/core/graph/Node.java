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

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a node, that is a step of the Crowd Pulse process.
 *
 * @author Francesco Pontillo
 */
public class Node {
    private String name;
    private String plugin;
    private JsonObject config;
    private transient List<Node> prev;
    private transient List<Node> next;
    private transient Graph graph;

    protected boolean _wasBuilt;

    /**
     * Get the name of the Node.
     *
     * @return The name of the Node.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Node.
     *
     * @param name The name of the Node.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the actual {@link net.frakbot.crowdpulse.common.util.spi.IPlugin} implementation to instantiate.
     *
     * @return The plugin name to use for the step.
     */
    public String getPlugin() {
        return plugin;
    }

    /**
     * Set the actual {@link net.frakbot.crowdpulse.common.util.spi.IPlugin} implementation to instantiate.
     *
     * @param plugin The plugin name to use for the step.
     */
    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the generic configuration object for the step.
     *
     * @return A configuration object as a {@link JsonObject}.
     */
    public JsonObject getConfig() {
        return config;
    }

    /**
     * Set the configuration for the step.
     *
     * @param config A configuration object as a {@link JsonObject}.
     */
    public void setConfig(JsonObject config) {
        this.config = config;
    }

    /**
     * Get the {@link List} of the previous Nodes in the {@link Graph}.
     *
     * @return {@link List} of the previous Nodes.
     */
    public List<Node> getPrev() {
        return prev;
    }

    /**
     * Set the {@link List} of the previous Nodes in the {@link Graph}.
     *
     * @param prev {@link List} of the previous Nodes.
     */
    public void setPrev(List<Node> prev) {
        // save the current node state in the graph (can be root)
        boolean wasRoot = !this.hasPrev();

        this.prev = prev;

        // update the root nodes
        if (wasRoot) {
            graph.updateRootNodes();
        }
    }

    /**
     * Add a Node to the {@link List} of previous nodes.
     *
     * @param prev One of the Nodes previous to the current one.
     */
    public void addPrev(Node prev) {
        if (this.prev == null) {
            this.prev = new ArrayList<>();
        }

        // save the current node state in the graph (can be root)
        boolean wasRoot = !this.hasPrev();

        // add the previous node
        this.prev.add(prev);

        // update the root nodes
        if (wasRoot) {
            graph.updateRootNodes();
        }
    }

    /**
     * Check if the Node has previous elements in the {@link Graph}.
     *
     * @return {@code true} if the Node has previous elements, {@code false} otherwise.
     */
    public boolean hasPrev() {
        return (this.prev != null && this.prev.size() > 0);
    }

    /**
     * Get the {@link List} of the next Nodes in the {@link Graph}.
     *
     * @return {@link List} of the following Nodes.
     */
    public List<Node> getNext() {
        return next;
    }

    /**
     * Set the {@link List} of the next Nodes in the {@link Graph}.
     *
     * @param next {@link List} of the following Nodes.
     */
    public void setNext(List<Node> next) {
        // save the current node state in the graph (can be a terminal)
        boolean wasTerminal = !this.hasNext();

        this.next = next;

        // conditionally update terminals
        if (wasTerminal) {
            graph.updateTerminalNodes();
        }
    }

    /**
     * Add a Node to the {@link List} of following nodes.
     *
     * @param next One of the Nodes following the current one.
     */
    public void addNext(Node next) {
        if (this.next == null) {
            this.next = new ArrayList<>();
        }

        // save the current node state in the graph (can be a terminal)
        boolean wasTerminal = !this.hasNext();

        // add the next node
        this.next.add(next);

        // conditionally update terminals
        if (wasTerminal) {
            graph.updateTerminalNodes();
        }
    }

    /**
     * Check if the Node has following elements in the {@link Graph}.
     *
     * @return {@code true} if the Node has following elements, {@code false} otherwise.
     */
    public boolean hasNext() {
        return (this.next != null && this.next.size() > 0);
    }

    /**
     * Get the {@link Graph} this Node is contained in.
     *
     * @return The container {@link Graph}.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Set the {@link Graph} this Node is contained in.
     *
     * @param graph The container {@link Graph}.
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

}
