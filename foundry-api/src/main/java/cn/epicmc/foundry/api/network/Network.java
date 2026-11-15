package cn.epicmc.foundry.api.network;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a connected network of nodes.
 * <p>
 * A network manages topology and coordinates transfers between nodes.
 * Networks are created and maintained automatically by the NetworkManager.
 * <p>
 * <b>Thread Safety:</b> All methods are thread-safe and can be called from
 * any thread, including virtual threads.
 *
 * @since 1.0.0
 */
public interface Network {

    /**
     * Get the unique identifier of this network.
     *
     * @return The network UUID
     */
    UUID getNetworkId();

    /**
     * Get the type of this network.
     *
     * @return The network type
     */
    NetworkType getNetworkType();

    /**
     * Get all nodes in this network.
     * <p>
     * <b>Thread Safety:</b> Returns an immutable snapshot of the current nodes.
     *
     * @return Immutable set of node IDs
     */
    Set<UUID> getNodes();

    /**
     * Get the number of nodes in this network.
     *
     * @return The node count
     */
    int getNodeCount();

    /**
     * Check if this network contains a specific node.
     *
     * @param nodeId The node ID to check
     * @return true if the node is in this network
     */
    boolean containsNode(UUID nodeId);

    /**
     * Get a node by its ID.
     *
     * @param nodeId The node ID
     * @return The node, or empty if not found
     */
    Optional<NetworkNode> getNode(UUID nodeId);

    /**
     * Check if this network is still valid.
     * <p>
     * A network becomes invalid when it has no nodes or has been merged into another network.
     *
     * @return true if valid
     */
    boolean isValid();

    /**
     * Request a network topology update.
     * <p>
     * This triggers an incremental recalculation of the network graph.
     * The operation runs asynchronously on a virtual thread.
     *
     * @return A future that completes when the update is done
     */
    CompletableFuture<Void> requestUpdate();

    /**
     * Get statistics about this network.
     *
     * @return Network statistics
     */
    NetworkStats getStats();
}
