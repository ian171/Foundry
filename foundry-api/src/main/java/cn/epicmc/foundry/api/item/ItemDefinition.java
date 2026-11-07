package cn.epicmc.foundry.api.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

/**
 * Defines a custom item in the Foundry framework.
 * <p>
 * Item definitions represent the static, type-level properties of an item.
 * Unlike blocks, items are represented using vanilla ItemStacks with custom NBT data
 * to identify them as Foundry items.
 * <p>
 * <b>Identification Strategy:</b>
 * <ul>
 *   <li>Use {@link Material} as base type</li>
 *   <li>Store Foundry ID in ItemStack NBT</li>
 *   <li>Support custom model data for resource packs</li>
 *   <li>Optional: Use player heads for custom textures</li>
 * </ul>
 *
 * @see ItemStack
 */
public interface ItemDefinition {

    /**
     * Get the unique identifier of this item definition.
     * <p>
     * Example: {@code example:copper_ingot}
     *
     * @return The unique namespaced key
     */
    NamespacedKey getId();

    /**
     * Get the base material used for this item.
     * <p>
     * This is the vanilla Minecraft material that represents the item.
     * Common choices:
     * <ul>
     *   <li>Standard materials: {@link Material#IRON_INGOT}, {@link Material#DIAMOND}, etc</li>
     *   <li>Custom model: {@link Material#PAPER}, {@link Material#STICK} with custom model data</li>
     *   <li>Custom texture: {@link Material#PLAYER_HEAD} with custom skull texture</li>
     * </ul>
     *
     * @return The base Minecraft material
     */
    Material getBaseMaterial();

    /**
     * Get the display name of this item.
     * <p>
     * This is shown to players in inventory tooltips.
     * Should be localized if internationalization is supported.
     *
     * @return The human-readable display name
     */
    String getDisplayName();

    /**
     * Get the lore (description) lines for this item.
     * <p>
     * Lore is displayed below the display name in tooltips.
     * Each string represents one line.
     *
     * @return The lore lines, or empty array for no lore
     */
    default String[] getLore() {
        return new String[0];
    }

    /**
     * Get the custom model data value for resource pack models.
     * <p>
     * This is used by resource packs to apply custom item models.
     * Return 0 if no custom model is used.
     * <p>
     * Example: A copper wrench might use custom model data 100001
     *
     * @return The custom model data value, or 0 for none
     */
    default int getCustomModelData() {
        return 0;
    }

    /**
     * Get the maximum stack size for this item.
     * <p>
     * This overrides the vanilla stack size for the base material.
     * Valid range: 1-64
     *
     * @return The maximum stack size
     */
    default int getMaxStackSize() {
        return 64;
    }

    /**
     * Check if this item represents a placeable block.
     * <p>
     * If true, {@link #getBlockDefinition()} must return a valid block definition.
     *
     * @return {@code true} if this item places a block, {@code false} otherwise
     */
    default boolean isBlockItem() {
        return false;
    }

    /**
     * Get the block definition that this item places, if applicable.
     * <p>
     * Only relevant if {@link #isBlockItem()} returns true.
     *
     * @return The block definition ID, or null if not a block item
     */
    default NamespacedKey getBlockDefinition() {
        return null;
    }

    /**
     * Create an ItemStack from this definition.
     * <p>
     * The created ItemStack will have:
     * <ul>
     *   <li>Correct base material</li>
     *   <li>Display name and lore</li>
     *   <li>Custom model data (if any)</li>
     *   <li>Foundry identification NBT</li>
     * </ul>
     *
     * @param amount The stack size
     * @return A new ItemStack representing this item
     */
    ItemStack createItemStack(int amount);

    /**
     * Create a single ItemStack from this definition.
     * <p>
     * Equivalent to {@code createItemStack(1)}
     *
     * @return A new ItemStack with amount 1
     */
    default ItemStack createItemStack() {
        return createItemStack(1);
    }
}
