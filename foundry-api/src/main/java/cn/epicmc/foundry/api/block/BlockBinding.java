package cn.epicmc.foundry.api.block;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.util.UUID;

/**
 * Binds a logical block instance to a physical world location.
 * <p>
 * The binding system maintains the relationship between:
 * <ul>
 *   <li>Physical Block (World + X,Y,Z coordinates)</li>
 *   <li>Logical Block Instance (UUID + Definition + State)</li>
 * </ul>
 * <p>
 * This allows the framework to:
 * <ul>
 *   <li>Find the machine instance when a block is interacted with</li>
 *   <li>Detect when a physical block is destroyed</li>
 *   <li>Clean up instances when blocks are removed</li>
 * </ul>
 * <p>
 * <b>Storage Format:</b>
 * <pre>
 * World UUID
 *     ├─ X
 *     ├─ Y
 *     ├─ Z
 *     ├─ Machine UUID
 *     └─ Definition ID
 * </pre>
 *
 * @see BlockInstance
 * @see BlockDefinition
 */
public interface BlockBinding {

    /**
     * Get the world location of the physical block.
     *
     * @return The block location
     */
    Location getLocation();

    /**
     * Get the UUID of the bound block instance.
     *
     * @return The instance UUID
     */
    UUID getInstanceId();

    /**
     * Get the definition ID of the block.
     *
     * @return The definition ID
     */
    NamespacedKey getDefinitionId();

    /**
     * Check if the binding is still valid.
     * <p>
     * A binding becomes invalid if:
     * <ul>
     *   <li>The physical block no longer matches the expected type</li>
     *   <li>The block instance was removed</li>
     *   <li>The chunk is corrupted</li>
     * </ul>
     *
     * @return {@code true} if valid, {@code false} if should be removed
     */
    boolean isValid();
}
