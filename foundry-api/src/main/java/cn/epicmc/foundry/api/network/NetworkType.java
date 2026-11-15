package cn.epicmc.foundry.api.network;

import org.bukkit.NamespacedKey;

/**
 * Represents a type of network that can transfer content between nodes.
 * <p>
 * Network types define what kind of content can be transferred (energy, fluid, items, etc.)
 * and are used to ensure nodes can only connect to compatible networks.
 * <p>
 * <b>Thread Safety:</b> Implementations must be immutable and thread-safe.
 *
 * @since 1.0.0
 */
public interface NetworkType {

    /**
     * Get the unique identifier for this network type.
     *
     * @return The network type ID
     */
    NamespacedKey getId();

    /**
     * Get the human-readable name of this network type.
     *
     * @return The display name
     */
    String getDisplayName();

    /**
     * Check if this network type can connect to another network type.
     * <p>
     * This allows for cross-network connections (e.g., energy converters).
     *
     * @param other The other network type
     * @return true if connection is allowed
     */
    default boolean canConnectTo(NetworkType other) {
        return this.getId().equals(other.getId());
    }
}
