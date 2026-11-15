package cn.epicmc.foundry.network;

import cn.epicmc.foundry.api.network.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Thread-safe implementation of a network.
 * <p>
 * This class manages the topology of connected nodes and coordinates
 * updates using virtual threads for performance.
 *
 * @since 1.0.0
 */
public class SimpleNetwork implements Network {

    private final UUID networkId;
    private final NetworkType networkType;
    private final Map<UUID, NetworkNode> nodes;
    private final AtomicBoolean valid;
    private final AtomicLong lastUpdateTime;
    private final Logger logger;

    // Statistics
    private volatile int connectionCount;
    private volatile double transfersPerTick;

    public SimpleNetwork(UUID networkId, NetworkType networkType, Logger logger) {
        this.networkId = Objects.requireNonNull(networkId, "networkId");
        this.networkType = Objects.requireNonNull(networkType, "networkType");
        this.logger = Objects.requireNonNull(logger, "logger");
        this.nodes = new ConcurrentHashMap<>();
        this.valid = new AtomicBoolean(true);
        this.lastUpdateTime = new AtomicLong(System.currentTimeMillis());
        this.connectionCount = 0;
        this.transfersPerTick = 0.0;
    }

    @Override
    public UUID getNetworkId() {
        return networkId;
    }

    @Override
    public NetworkType getNetworkType() {
        return networkType;
    }

    @Override
    public Set<UUID> getNodes() {
        return Set.copyOf(nodes.keySet());
    }

    @Override
    public int getNodeCount() {
        return nodes.size();
    }

    @Override
    public boolean containsNode(UUID nodeId) {
        return nodes.containsKey(nodeId);
    }

    @Override
    public Optional<NetworkNode> getNode(UUID nodeId) {
        return Optional.ofNullable(nodes.get(nodeId));
    }

    @Override
    public boolean isValid() {
        return valid.get() && !nodes.isEmpty();
    }

    @Override
    public CompletableFuture<Void> requestUpdate() {
        return CompletableFuture.runAsync(() -> {
            try {
                performUpdate();
            } catch (Exception e) {
                logger.warning("Failed to update network " + networkId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }, VirtualThreadExecutor.getInstance());
    }

    @Override
    public NetworkStats getStats() {
        return new NetworkStats(
            nodes.size(),
            connectionCount,
            transfersPerTick,
            lastUpdateTime.get(),
            isValid()
        );
    }

    /**
     * Add a node to this network.
     * <p>
     * <b>Thread Safety:</b> This method is thread-safe.
     *
     * @param node The node to add
     */
    public void addNode(NetworkNode node) {
        Objects.requireNonNull(node, "node");

        if (!node.getNetworkType().canConnectTo(networkType)) {
            throw new IllegalArgumentException(
                "Node type " + node.getNetworkType().getId() +
                " cannot connect to network type " + networkType.getId()
            );
        }

        nodes.put(node.getNodeId(), node);
        lastUpdateTime.set(System.currentTimeMillis());

        logger.fine("Added node " + node.getNodeId() + " to network " + networkId);
    }

    /**
     * Remove a node from this network.
     * <p>
     * <b>Thread Safety:</b> This method is thread-safe.
     *
     * @param nodeId The ID of the node to remove
     * @return true if the node was removed
     */
    public boolean removeNode(UUID nodeId) {
        NetworkNode removed = nodes.remove(nodeId);

        if (removed != null) {
            lastUpdateTime.set(System.currentTimeMillis());
            logger.fine("Removed node " + nodeId + " from network " + networkId);

            // Mark as invalid if empty
            if (nodes.isEmpty()) {
                valid.set(false);
            }

            return true;
        }

        return false;
    }

    /**
     * Invalidate this network.
     * <p>
     * Called when the network is merged into another or no longer needed.
     */
    public void invalidate() {
        valid.set(false);
        logger.fine("Network " + networkId + " invalidated");
    }

    /**
     * Merge another network into this one.
     * <p>
     * All nodes from the other network are moved to this network.
     * <p>
     * <b>Thread Safety:</b> This method is thread-safe.
     *
     * @param other The network to merge
     */
    public void merge(SimpleNetwork other) {
        Objects.requireNonNull(other, "other");

        if (!networkType.canConnectTo(other.networkType)) {
            throw new IllegalArgumentException(
                "Cannot merge network of type " + other.networkType.getId() +
                " into network of type " + networkType.getId()
            );
        }

        // Move all nodes
        other.nodes.forEach((id, node) -> {
            nodes.put(id, node);
        });

        // Invalidate the other network
        other.invalidate();
        other.nodes.clear();

        lastUpdateTime.set(System.currentTimeMillis());

        logger.info("Merged network " + other.networkId + " into " + networkId +
                   " (" + nodes.size() + " total nodes)");
    }

    /**
     * Perform incremental network update.
     * <p>
     * Recalculates connections and validates nodes.
     */
    private void performUpdate() {
        long startTime = System.currentTimeMillis();

        // Remove invalid nodes
        List<UUID> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, NetworkNode> entry : nodes.entrySet()) {
            if (!entry.getValue().isValid()) {
                toRemove.add(entry.getKey());
            }
        }

        toRemove.forEach(this::removeNode);

        // Recalculate connection count
        int totalConnections = 0;
        for (NetworkNode node : nodes.values()) {
            totalConnections += node.getConnections().size();
        }
        // Each connection is counted twice (once from each end)
        connectionCount = totalConnections / 2;

        lastUpdateTime.set(System.currentTimeMillis());

        long elapsed = System.currentTimeMillis() - startTime;
        logger.fine("Updated network " + networkId + " in " + elapsed + "ms" +
                   " (" + nodes.size() + " nodes, " + connectionCount + " connections)");
    }

    @Override
    public String toString() {
        return "Network{" +
               "id=" + networkId +
               ", type=" + networkType.getId() +
               ", nodes=" + nodes.size() +
               ", valid=" + valid.get() +
               '}';
    }
}
