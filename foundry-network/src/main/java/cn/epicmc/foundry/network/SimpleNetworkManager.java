package cn.epicmc.foundry.network;

import cn.epicmc.foundry.api.network.*;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Thread-safe implementation of NetworkManager using virtual threads.
 * <p>
 * This class coordinates all network operations and ensures thread safety
 * through a combination of concurrent data structures and read-write locks.
 *
 * @since 1.0.0
 */
public class SimpleNetworkManager implements NetworkManager {

    private final Logger logger;

    // Thread-safe data structures
    private final Map<UUID, NetworkNode> nodes;
    private final Map<UUID, SimpleNetwork> networks;
    private final Map<UUID, UUID> nodeToNetwork; // nodeId -> networkId
    private final Map<Location, UUID> locationToNode; // location -> nodeId

    // Lock for write operations that modify topology
    private final ReadWriteLock topologyLock;

    // Statistics
    private volatile long lastCleanupTime;
    private volatile int totalUpdates;

    public SimpleNetworkManager(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
        this.nodes = new ConcurrentHashMap<>();
        this.networks = new ConcurrentHashMap<>();
        this.nodeToNetwork = new ConcurrentHashMap<>();
        this.locationToNode = new ConcurrentHashMap<>();
        this.topologyLock = new ReentrantReadWriteLock();
        this.lastCleanupTime = System.currentTimeMillis();
        this.totalUpdates = 0;
    }

    @Override
    public CompletableFuture<Network> registerNode(NetworkNode node) {
        Objects.requireNonNull(node, "node");

        return CompletableFuture.supplyAsync(() -> {
            topologyLock.writeLock().lock();
            try {
                // Register node
                nodes.put(node.getNodeId(), node);
                locationToNode.put(node.getLocation(), node.getNodeId());

                logger.fine("Registering node " + node.getNodeId() +
                           " at " + formatLocation(node.getLocation()));

                // Find adjacent compatible nodes
                Set<NetworkNode> adjacentNodes = findAdjacentNodes(node);

                if (adjacentNodes.isEmpty()) {
                    // Create new network for this node
                    SimpleNetwork network = createNetwork(node.getNetworkType());
                    network.addNode(node);
                    nodeToNetwork.put(node.getNodeId(), network.getNetworkId());

                    logger.info("Created new network " + network.getNetworkId() +
                               " for node " + node.getNodeId());

                    return network;
                } else {
                    // Connect to adjacent nodes
                    connectNodes(node, adjacentNodes);

                    // Find or merge networks
                    Set<UUID> affectedNetworkIds = adjacentNodes.stream()
                        .map(NetworkNode::getNodeId)
                        .map(nodeToNetwork::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                    if (affectedNetworkIds.isEmpty()) {
                        // Create new network
                        SimpleNetwork network = createNetwork(node.getNetworkType());
                        network.addNode(node);
                        nodeToNetwork.put(node.getNodeId(), network.getNetworkId());

                        for (NetworkNode adj : adjacentNodes) {
                            network.addNode(adj);
                            nodeToNetwork.put(adj.getNodeId(), network.getNetworkId());
                        }

                        logger.info("Created network " + network.getNetworkId() +
                                   " with " + (adjacentNodes.size() + 1) + " nodes");

                        return network;
                    } else {
                        // Merge into existing network(s)
                        Iterator<UUID> it = affectedNetworkIds.iterator();
                        UUID primaryNetworkId = it.next();
                        SimpleNetwork primaryNetwork = networks.get(primaryNetworkId);

                        // Add the new node
                        primaryNetwork.addNode(node);
                        nodeToNetwork.put(node.getNodeId(), primaryNetworkId);

                        // Merge other networks into primary
                        while (it.hasNext()) {
                            UUID otherNetworkId = it.next();
                            SimpleNetwork otherNetwork = networks.get(otherNetworkId);

                            if (otherNetwork != null && !otherNetworkId.equals(primaryNetworkId)) {
                                // Update node mappings
                                for (UUID nodeId : otherNetwork.getNodes()) {
                                    nodeToNetwork.put(nodeId, primaryNetworkId);
                                }

                                // Merge networks
                                primaryNetwork.merge(otherNetwork);
                                networks.remove(otherNetworkId);
                            }
                        }

                        logger.info("Added node " + node.getNodeId() +
                                   " to network " + primaryNetworkId +
                                   " (merged " + affectedNetworkIds.size() + " networks)");

                        return primaryNetwork;
                    }
                }
            } finally {
                topologyLock.writeLock().unlock();
            }
        }, VirtualThreadExecutor.getInstance());
    }

    @Override
    public CompletableFuture<Void> unregisterNode(UUID nodeId) {
        Objects.requireNonNull(nodeId, "nodeId");

        return CompletableFuture.runAsync(() -> {
            topologyLock.writeLock().lock();
            try {
                NetworkNode node = nodes.remove(nodeId);
                if (node == null) {
                    logger.warning("Attempted to unregister unknown node: " + nodeId);
                    return;
                }

                locationToNode.remove(node.getLocation());

                logger.fine("Unregistering node " + nodeId);

                // Find the network this node belongs to
                UUID networkId = nodeToNetwork.remove(nodeId);
                if (networkId == null) {
                    return;
                }

                SimpleNetwork network = networks.get(networkId);
                if (network == null) {
                    return;
                }

                // Disconnect from all neighbors
                if (node instanceof SimpleNetworkNode simpleNode) {
                    for (UUID neighborId : node.getConnections()) {
                        NetworkNode neighbor = nodes.get(neighborId);
                        if (neighbor instanceof SimpleNetworkNode simpleNeighbor) {
                            simpleNeighbor.removeConnection(nodeId);
                        }
                    }
                    simpleNode.clearConnections();
                }

                // Remove from network
                network.removeNode(nodeId);

                // Check if network split into multiple networks
                if (network.getNodeCount() > 0) {
                    checkNetworkSplit(network);
                } else {
                    // Network is now empty, remove it
                    networks.remove(networkId);
                    logger.info("Removed empty network " + networkId);
                }

                logger.info("Unregistered node " + nodeId + " from network " + networkId);

            } finally {
                topologyLock.writeLock().unlock();
            }
        }, VirtualThreadExecutor.getInstance());
    }

    @Override
    public Optional<NetworkNode> getNode(UUID nodeId) {
        return Optional.ofNullable(nodes.get(nodeId));
    }

    @Override
    public Optional<NetworkNode> getNodeAt(Location location) {
        UUID nodeId = locationToNode.get(location);
        return nodeId != null ? Optional.ofNullable(nodes.get(nodeId)) : Optional.empty();
    }

    @Override
    public Optional<Network> getNetworkForNode(UUID nodeId) {
        UUID networkId = nodeToNetwork.get(nodeId);
        return networkId != null ? Optional.ofNullable(networks.get(networkId)) : Optional.empty();
    }

    @Override
    public Optional<Network> getNetwork(UUID networkId) {
        return Optional.ofNullable(networks.get(networkId));
    }

    @Override
    public Set<Network> getNetworksByType(NetworkType type) {
        return networks.values().stream()
            .filter(net -> net.getNetworkType().equals(type))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Network> getAllNetworks() {
        return Set.copyOf(networks.values());
    }

    @Override
    public Set<UUID> getAllNodes() {
        return Set.copyOf(nodes.keySet());
    }

    @Override
    public CompletableFuture<Void> rebuildAllNetworks() {
        return CompletableFuture.runAsync(() -> {
            topologyLock.writeLock().lock();
            try {
                logger.info("Rebuilding all networks...");

                long startTime = System.currentTimeMillis();

                // Clear existing networks
                networks.values().forEach(SimpleNetwork::invalidate);
                networks.clear();
                nodeToNetwork.clear();

                // Rebuild from nodes
                for (NetworkNode node : nodes.values()) {
                    if (!nodeToNetwork.containsKey(node.getNodeId())) {
                        buildNetworkFrom(node);
                    }
                }

                long elapsed = System.currentTimeMillis() - startTime;
                logger.info("Rebuilt " + networks.size() + " networks in " + elapsed + "ms");

            } finally {
                topologyLock.writeLock().unlock();
            }
        }, VirtualThreadExecutor.getInstance());
    }

    @Override
    public CompletableFuture<Void> cleanup() {
        return CompletableFuture.runAsync(() -> {
            topologyLock.writeLock().lock();
            try {
                logger.fine("Running network cleanup...");

                long startTime = System.currentTimeMillis();
                int removedNodes = 0;
                int removedNetworks = 0;

                // Remove invalid nodes
                List<UUID> invalidNodes = nodes.entrySet().stream()
                    .filter(entry -> !entry.getValue().isValid())
                    .map(Map.Entry::getKey)
                    .toList();

                for (UUID nodeId : invalidNodes) {
                    unregisterNode(nodeId).join();
                    removedNodes++;
                }

                // Remove invalid networks
                List<UUID> invalidNetworks = networks.entrySet().stream()
                    .filter(entry -> !entry.getValue().isValid())
                    .map(Map.Entry::getKey)
                    .toList();

                for (UUID networkId : invalidNetworks) {
                    networks.remove(networkId);
                    removedNetworks++;
                }

                lastCleanupTime = System.currentTimeMillis();
                long elapsed = lastCleanupTime - startTime;

                if (removedNodes > 0 || removedNetworks > 0) {
                    logger.info("Cleanup removed " + removedNodes + " nodes and " +
                               removedNetworks + " networks in " + elapsed + "ms");
                }

            } finally {
                topologyLock.writeLock().unlock();
            }
        }, VirtualThreadExecutor.getInstance());
    }

    @Override
    public GlobalNetworkStats getGlobalStats() {
        int totalConnections = nodes.values().stream()
            .mapToInt(node -> node.getConnections().size())
            .sum() / 2; // Divide by 2 since each connection is counted twice

        double avgNetworkSize = networks.isEmpty() ? 0.0 :
            (double) nodes.size() / networks.size();

        return new GlobalNetworkStats(
            networks.size(),
            nodes.size(),
            totalConnections,
            avgNetworkSize,
            totalUpdates / Math.max(1, (System.currentTimeMillis() - lastCleanupTime) / 1000.0),
            VirtualThreadExecutor.getInstance().getActiveThreadCount()
        );
    }

    // ==================== Helper Methods ====================

    private SimpleNetwork createNetwork(NetworkType type) {
        UUID networkId = UUID.randomUUID();
        SimpleNetwork network = new SimpleNetwork(networkId, type, logger);
        networks.put(networkId, network);
        return network;
    }

    private Set<NetworkNode> findAdjacentNodes(NetworkNode node) {
        Set<NetworkNode> adjacent = new HashSet<>();
        Location loc = node.getLocation();

        // Check all 6 directions
        int[][] offsets = {
            {1, 0, 0}, {-1, 0, 0},
            {0, 1, 0}, {0, -1, 0},
            {0, 0, 1}, {0, 0, -1}
        };

        for (int[] offset : offsets) {
            Location checkLoc = loc.clone().add(offset[0], offset[1], offset[2]);
            UUID neighborId = locationToNode.get(checkLoc);

            if (neighborId != null) {
                NetworkNode neighbor = nodes.get(neighborId);
                if (neighbor != null &&
                    neighbor.getNetworkType().canConnectTo(node.getNetworkType())) {
                    adjacent.add(neighbor);
                }
            }
        }

        return adjacent;
    }

    private void connectNodes(NetworkNode node, Set<NetworkNode> neighbors) {
        if (!(node instanceof SimpleNetworkNode simpleNode)) {
            return;
        }

        for (NetworkNode neighbor : neighbors) {
            if (neighbor instanceof SimpleNetworkNode simpleNeighbor) {
                simpleNode.addConnection(neighbor.getNodeId());
                simpleNeighbor.addConnection(node.getNodeId());
            }
        }
    }

    private void buildNetworkFrom(NetworkNode startNode) {
        SimpleNetwork network = createNetwork(startNode.getNetworkType());
        Set<UUID> visited = new HashSet<>();
        Queue<NetworkNode> queue = new LinkedList<>();

        queue.add(startNode);
        visited.add(startNode.getNodeId());

        while (!queue.isEmpty()) {
            NetworkNode current = queue.poll();
            network.addNode(current);
            nodeToNetwork.put(current.getNodeId(), network.getNetworkId());

            for (UUID neighborId : current.getConnections()) {
                if (!visited.contains(neighborId)) {
                    NetworkNode neighbor = nodes.get(neighborId);
                    if (neighbor != null &&
                        neighbor.getNetworkType().canConnectTo(current.getNetworkType())) {
                        visited.add(neighborId);
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    private void checkNetworkSplit(SimpleNetwork network) {
        Set<UUID> allNodes = network.getNodes();
        if (allNodes.isEmpty()) {
            return;
        }

        // Use BFS to find connected components
        Set<UUID> visited = new HashSet<>();
        List<Set<UUID>> components = new ArrayList<>();

        for (UUID nodeId : allNodes) {
            if (!visited.contains(nodeId)) {
                Set<UUID> component = new HashSet<>();
                Queue<UUID> queue = new LinkedList<>();

                queue.add(nodeId);
                visited.add(nodeId);

                while (!queue.isEmpty()) {
                    UUID currentId = queue.poll();
                    component.add(currentId);

                    NetworkNode current = nodes.get(currentId);
                    if (current != null) {
                        for (UUID neighborId : current.getConnections()) {
                            if (!visited.contains(neighborId) && allNodes.contains(neighborId)) {
                                visited.add(neighborId);
                                queue.add(neighborId);
                            }
                        }
                    }
                }

                components.add(component);
            }
        }

        // If split into multiple components, create new networks
        if (components.size() > 1) {
            logger.info("Network " + network.getNetworkId() +
                       " split into " + components.size() + " networks");

            // Keep the first component in the original network
            Set<UUID> primaryComponent = components.get(0);
            for (UUID nodeId : new HashSet<>(allNodes)) {
                if (!primaryComponent.contains(nodeId)) {
                    network.removeNode(nodeId);
                }
            }

            // Create new networks for other components
            for (int i = 1; i < components.size(); i++) {
                SimpleNetwork newNetwork = createNetwork(network.getNetworkType());
                for (UUID nodeId : components.get(i)) {
                    NetworkNode node = nodes.get(nodeId);
                    if (node != null) {
                        newNetwork.addNode(node);
                        nodeToNetwork.put(nodeId, newNetwork.getNetworkId());
                    }
                }
            }
        }
    }

    private String formatLocation(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}
