package cn.epicmc.foundry.api.network;

import org.bukkit.Location;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a node in a network graph.
 * <p>
 * A network node is typically a block (machine, pipe, cable, etc.) that can
 * participate in network operations such as energy/fluid/item transfer.
 * <p>
 * <b>Thread Safety:</b> All methods must be safe to call from any thread.
 * Implementations should use appropriate synchronization.
 *
 * @since 1.0.0
 */
public interface NetworkNode {

    /**
     * Get the unique identifier of this node.
     * <p>
     * This ID is persistent and survives server restarts.
     *
     * @return The node UUID
     */
    UUID getNodeId();

    /**
     * Get the type of network this node belongs to.
     *
     * @return The network type
     */
    NetworkType getNetworkType();

    /**
     * Get the world location of this node.
     * <p>
     * <b>Thread Safety:</b> Returns a cloned location to prevent external modification.
     *
     * @return The node location
     */
    Location getLocation();

    /**
     * Get the IDs of all nodes this node is connected to.
     * <p>
     * <b>Thread Safety:</b> Returns an immutable copy of the connection set.
     *
     * @return Immutable set of connected node IDs
     */
    Set<UUID> getConnections();

    /**
     * Check if this node is currently valid and loaded.
     * <p>
     * A node becomes invalid when:
     * <ul>
     *   <li>The block is broken</li>
     *   <li>The chunk is unloaded</li>
     *   <li>The world is unloaded</li>
     * </ul>
     *
     * @return true if the node is valid
     */
    boolean isValid();

    /**
     * Check if this node can accept incoming transfers.
     *
     * @return true if this node can receive content
     */
    boolean canReceive();

    /**
     * Check if this node can send outgoing transfers.
     *
     * @return true if this node can send content
     */
    boolean canSend();

    /**
     * Get the priority of this node for transfer operations.
     * <p>
     * Higher priority nodes are processed first.
     * Default priority is 0.
     *
     * @return The priority value
     */
    default int getPriority() {
        return 0;
    }
}
