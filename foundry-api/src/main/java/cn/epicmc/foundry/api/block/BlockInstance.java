package cn.epicmc.foundry.api.block;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.util.UUID;

/**
 * Represents a placed block in the world.
 * <p>
 * Block instances are the runtime representation of blocks. They maintain:
 * <ul>
 *   <li>World position</li>
 *   <li>Runtime state</li>
 *   <li>Reference to their definition</li>
 * </ul>
 * <p>
 * <b>Lifecycle:</b>
 * <pre>
 * Place Block
 *      ↓
 * Create Instance
 *      ↓
 * Register Binding (Location → Instance)
 *      ↓
 * Load State (from database)
 *      ↓
 * Activate
 *      ↓
 * Runtime Operations
 *      ↓
 * Break Block
 *      ↓
 * Save State
 *      ↓
 * Remove Instance
 *      ↓
 * Unregister Binding
 * </pre>
 *
 * @see BlockDefinition
 * @see BlockBinding
 */
public interface BlockInstance {

    /**
     * Get the unique identifier of this specific block instance.
     * <p>
     * Each placed block has a unique UUID that persists across server restarts.
     * This is used to identify the block in the database and for tracking purposes.
     *
     * @return The unique identifier
     */
    UUID getUniqueId();

    /**
     * Get the definition ID of this block.
     * <p>
     * This identifies what type of block this is (e.g., "example:generator").
     * The definition contains the static, type-level properties.
     *
     * @return The definition's namespaced key
     */
    NamespacedKey getDefinitionId();

    /**
     * Get the location of this block in the world.
     * <p>
     * <b>Thread Safety Warning:</b> The returned Location contains a reference
     * to the World, which is not thread-safe. If you need to pass this location
     * to another thread, clone it or extract only the coordinates.
     *
     * @return The world location
     */
    Location getLocation();

    /**
     * Get the current state of this block.
     * <p>
     * Block states represent the operational status:
     * <ul>
     *   <li>IDLE - Not doing anything</li>
     *   <li>ACTIVE - Currently operating</li>
     *   <li>PAUSED - Temporarily stopped</li>
     *   <li>ERROR - In an error state</li>
     * </ul>
     *
     * @return The current block state
     */
    BlockState getState();

    /**
     * Check if this block is currently loaded.
     * <p>
     * A block is loaded if its chunk is loaded. Unloaded blocks cannot be ticked
     * or interacted with, but their data is preserved.
     *
     * @return {@code true} if the block is loaded, {@code false} otherwise
     */
    boolean isLoaded();

    /**
     * Check if this block is valid (still exists in the world).
     * <p>
     * A block becomes invalid if:
     * <ul>
     *   <li>It was broken by a player</li>
     *   <li>It was destroyed by an explosion</li>
     *   <li>The physical block was replaced by another plugin</li>
     *   <li>WorldEdit or similar tools modified the block</li>
     * </ul>
     * <p>
     * Invalid blocks should be cleaned up and removed from the registry.
     *
     * @return {@code true} if the block is valid, {@code false} if it should be removed
     */
    boolean isValid();
}
