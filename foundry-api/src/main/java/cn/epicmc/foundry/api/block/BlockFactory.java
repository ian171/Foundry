package cn.epicmc.foundry.api.block;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Factory for creating block instances.
 * <p>
 * Each block definition has an associated factory that creates new instances
 * when the block is placed in the world.
 * <p>
 * The factory is responsible for:
 * <ul>
 *   <li>Creating new block/machine instances</li>
 *   <li>Initializing default state</li>
 *   <li>Setting up capabilities</li>
 *   <li>Applying placement context (direction, player data, etc)</li>
 * </ul>
 */
@FunctionalInterface
public interface BlockFactory {

    /**
     * Create a new block instance at the given location.
     * <p>
     * This is called when:
     * <ul>
     *   <li>A player places the block</li>
     *   <li>The block is placed programmatically</li>
     *   <li>World data is loaded and the block needs to be restored</li>
     * </ul>
     * <p>
     * The created instance should be initialized with default values.
     * State loading from the database happens separately.
     *
     * @param definition The block definition
     * @param location The world location where the block is placed
     * @param context Additional context about the placement
     * @return A new block instance
     */
    BlockInstance create(
        BlockDefinition definition,
        Location location,
        PlacementContext context
    );
}
