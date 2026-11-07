package cn.epicmc.foundry.api.block;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Context information for block placement.
 * <p>
 * Provides additional information about how a block was placed,
 * allowing the block to initialize itself appropriately.
 */
public interface PlacementContext {

    /**
     * Get the player who placed the block, if applicable.
     * <p>
     * This is empty if the block was placed programmatically.
     *
     * @return The player, or empty if not placed by a player
     */
    Optional<Player> getPlayer();

    /**
     * Get the face of the block that was clicked during placement.
     * <p>
     * This can be used to determine the orientation of the placed block.
     *
     * @return The clicked block face
     */
    BlockFace getClickedFace();

    /**
     * Get the face of the new block relative to the clicked block.
     * <p>
     * This is typically the opposite of the clicked face and represents
     * the direction the block is facing.
     *
     * @return The placement face
     */
    BlockFace getPlacementFace();

    /**
     * Get the item stack used to place this block.
     * <p>
     * This can contain custom NBT data or other information that should
     * be transferred to the placed block.
     *
     * @return The item stack, or empty if not placed from an item
     */
    Optional<ItemStack> getItemStack();

    /**
     * Check if this placement should replace an existing block.
     * <p>
     * True if the block is being placed into a replaceable block
     * (like tall grass, water, etc).
     *
     * @return {@code true} if replacing, {@code false} otherwise
     */
    boolean isReplacing();

    /**
     * Create a simple placement context.
     *
     * @param player The player placing the block, or null
     * @param clickedFace The clicked face
     * @return A new placement context
     */
    static PlacementContext create(Player player, BlockFace clickedFace) {
        return new SimplePlacementContext(player, clickedFace);
    }
}
