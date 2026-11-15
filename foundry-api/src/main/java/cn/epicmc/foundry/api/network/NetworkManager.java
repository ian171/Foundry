package cn.epicmc.foundry.api.network;

import org.bukkit.Location;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Manages all networks in the Foundry framework.
 * <p>
 * The NetworkManager is responsible for:
 * <ul>
 *   <li>Creating and destroying networks</li>
 *   <li>Managing network topology</li>
 *   <li>Handling node connections and disconnections</li>
 *   <li>Coordinating network merges and splits</li>
 *   <li>Scheduling network updates</li>
 * </ul>
 * <p>
 * <b>Thread Safety:</b> All methods are thread-safe and use virtual threads
 * for expensive operations to avoid blocking the main server thread.
 *
 * @since 1.0.0
 */
public interface NetworkManager {

    /**
     * Register a new node in the network system.
     * <p>
     * This will:
     * <ol>
     *   <li>Find adjacent compatible nodes</li>
     *   <li>Connect to them</li>
     *   <li>Merge into existing network or create a new one</li>
     * </ol>
     * <p>
     * The operation runs asynchronously on a virtual thread.
     *
     * @param node The node to register
     * @return A future that completes with the network the node was added to
     */
    CompletableFuture<Network> registerNode(NetworkNode node);

    /**
     * Unregister a node from the network system.
     * <p>
     * This will:
     * <ol>
     *   <li>Disconnect the node from all neighbors</li>
     *   <li>Check if the network splits into multiple networks</li>
     *   <li>Update affected networks</li>
     * </ol>
     * <p>
     * The operation runs asynchronously on a virtual thread.
     *
     * @param nodeId The ID of the node to unregister
     * @return A future that completes when the node is removed
     */
    CompletableFuture<Void> unregisterNode(UUID nodeId);

    /**
     * Get a node by its ID.
     *
     * @param nodeId The node ID
     * @return The node, or empty if not found
     */
    Optional<NetworkNode> getNode(UUID nodeId);

    /**
     * Get a node at a specific location.
     *
     * @param location The block location
     * @return The node at that location, or empty if none exists
     */
    Optional<NetworkNode> getNodeAt(Location location);

    /**
     * Get the network a node belongs to.
     *
     * @param nodeId The node ID
     * @return The network, or empty if the node is not in any network
     */
    Optional<Network> getNetworkForNode(UUID nodeId);

    /**
     * Get a network by its ID.
     *
     * @param networkId The network ID
     * @return The network, or empty if not found
     */
    Optional<Network> getNetwork(UUID networkId);

    /**
     * Get all networks of a specific type.
     *
     * @param type The network type
     * @return Immutable set of networks
     */
    Set<Network> getNetworksByType(NetworkType type);

    /**
     * Get all networks.
     *
     * @return Immutable set of all networks
     */
    Set<Network> getAllNetworks();

    /**
     * Get all registered nodes.
     *
     * @return Immutable set of all node IDs
     */
    Set<UUID> getAllNodes();

    /**
     * Trigger a full rebuild of all networks.
     * <p>
     * This should only be used when recovering from errors or during world load.
     * Normal operations should use incremental updates.
     *
     * @return A future that completes when rebuild is done
     */
    CompletableFuture<Void> rebuildAllNetworks();

    /**
     * Clean up invalid nodes and networks.
     * <p>
     * Removes nodes that are no longer valid (broken blocks, unloaded chunks, etc.)
     * and merges or splits networks as needed.
     *
     * @return A future that completes when cleanup is done
     */
    CompletableFuture<Void> cleanup();

    /**
     * Get statistics about all networks.
     *
     * @return Global network statistics
     */
    GlobalNetworkStats getGlobalStats();
}
