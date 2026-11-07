package cn.epicmc.foundry.api.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

/**
 * An item that places a block when used.
 * <p>
 * Block items create a link between items and blocks, allowing players
 * to carry and place custom blocks in their inventory.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * public class GeneratorBlockItem extends BlockItem {
 *
 *     public GeneratorBlockItem(NamespacedKey blockId) {
 *         super(
 *             new NamespacedKey("example", "generator"),
 *             Material.NOTE_BLOCK,
 *             "Electric Generator",
 *             blockId,
 *             new String[] {
 *                 "Generates electricity from fuel",
 *                 "Place in world to use"
 *             }
 *         );
 *     }
 * }
 * }</pre>
 */
public class BlockItem extends AbstractItem {

    private final NamespacedKey blockDefinitionId;

    /**
     * Create a new block item.
     *
     * @param id The unique identifier for this item
     * @param baseMaterial The Minecraft material to use
     * @param displayName The display name
     * @param blockDefinitionId The ID of the block this item places
     */
    public BlockItem(
        NamespacedKey id,
        Material baseMaterial,
        String displayName,
        NamespacedKey blockDefinitionId
    ) {
        super(id, baseMaterial, displayName);
        this.blockDefinitionId = blockDefinitionId;
    }

    /**
     * Create a new block item with lore.
     *
     * @param id The unique identifier for this item
     * @param baseMaterial The Minecraft material to use
     * @param displayName The display name
     * @param blockDefinitionId The ID of the block this item places
     * @param lore The lore lines
     */
    public BlockItem(
        NamespacedKey id,
        Material baseMaterial,
        String displayName,
        NamespacedKey blockDefinitionId,
        String... lore
    ) {
        super(id, baseMaterial, displayName, lore);
        this.blockDefinitionId = blockDefinitionId;
    }

    @Override
    public boolean isBlockItem() {
        return true;
    }

    @Override
    public NamespacedKey getBlockDefinition() {
        return blockDefinitionId;
    }
}
