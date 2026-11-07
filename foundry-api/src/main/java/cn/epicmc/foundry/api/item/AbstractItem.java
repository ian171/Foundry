package cn.epicmc.foundry.api.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base implementation of {@link ItemDefinition}.
 * <p>
 * Provides common functionality for item definitions with sensible defaults.
 * Third-party plugins can extend this class to create custom items.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * public class CopperIngot extends AbstractItem {
 *
 *     public CopperIngot() {
 *         super(
 *             new NamespacedKey("example", "copper_ingot"),
 *             Material.IRON_INGOT,
 *             "Copper Ingot"
 *         );
 *     }
 *
 *     @Override
 *     public int getCustomModelData() {
 *         return 100001;
 *     }
 * }
 * }</pre>
 */
public abstract class AbstractItem implements ItemDefinition {

    /**
     * NBT key for storing the Foundry item ID.
     */
    public static final NamespacedKey ITEM_ID_KEY =
        new NamespacedKey("foundry", "item_id");

    private final NamespacedKey id;
    private final Material baseMaterial;
    private final String displayName;
    private final String[] lore;
    private final int maxStackSize;

    /**
     * Create a new item definition with no lore.
     *
     * @param id The unique identifier
     * @param baseMaterial The Minecraft material to use
     * @param displayName The display name
     */
    protected AbstractItem(
        NamespacedKey id,
        Material baseMaterial,
        String displayName
    ) {
        this(id, baseMaterial, displayName, new String[0]);
    }

    /**
     * Create a new item definition with lore.
     *
     * @param id The unique identifier
     * @param baseMaterial The Minecraft material to use
     * @param displayName The display name
     * @param lore The lore lines
     */
    protected AbstractItem(
        NamespacedKey id,
        Material baseMaterial,
        String displayName,
        String... lore
    ) {
        this(id, baseMaterial, displayName, lore, 64);
    }

    /**
     * Create a new item definition with lore and custom stack size.
     *
     * @param id The unique identifier
     * @param baseMaterial The Minecraft material to use
     * @param displayName The display name
     * @param lore The lore lines
     * @param maxStackSize The maximum stack size
     */
    protected AbstractItem(
        NamespacedKey id,
        Material baseMaterial,
        String displayName,
        String[] lore,
        int maxStackSize
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.baseMaterial = Objects.requireNonNull(baseMaterial, "baseMaterial");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.lore = Objects.requireNonNull(lore, "lore");
        this.maxStackSize = maxStackSize;

        // Validate material is an item
        if (!baseMaterial.isItem()) {
            throw new IllegalArgumentException(
                "Base material must be an item: " + baseMaterial
            );
        }

        // Validate stack size
        if (maxStackSize < 1 || maxStackSize > 64) {
            throw new IllegalArgumentException(
                "Max stack size must be between 1 and 64: " + maxStackSize
            );
        }
    }

    @Override
    public NamespacedKey getId() {
        return id;
    }

    @Override
    public Material getBaseMaterial() {
        return baseMaterial;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String[] getLore() {
        return lore.clone(); // Return copy to maintain immutability
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public ItemStack createItemStack(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be positive: " + amount);
        }

        // Create base item stack
        ItemStack stack = new ItemStack(baseMaterial, amount);
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException(
                "Failed to get ItemMeta for " + baseMaterial
            );
        }

        // Set display name
        meta.setDisplayName(displayName);

        // Set lore if present
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }

        // Set custom model data if present
        int customModelData = getCustomModelData();
        if (customModelData != 0) {
            meta.setCustomModelData(customModelData);
        }

        // Store Foundry item ID in persistent data
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ITEM_ID_KEY, PersistentDataType.STRING, id.toString());

        // Apply meta to stack
        stack.setItemMeta(meta);

        return stack;
    }

    /**
     * Check if an ItemStack represents this Foundry item.
     *
     * @param stack The ItemStack to check
     * @return {@code true} if the stack is this item, {@code false} otherwise
     */
    public boolean matches(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String storedId = pdc.get(ITEM_ID_KEY, PersistentDataType.STRING);

        return id.toString().equals(storedId);
    }

    /**
     * Extract the Foundry item ID from an ItemStack, if present.
     *
     * @param stack The ItemStack to check
     * @return The Foundry item ID, or null if not a Foundry item
     */
    public static NamespacedKey getFoundryId(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String storedId = pdc.get(ITEM_ID_KEY, PersistentDataType.STRING);

        if (storedId == null) {
            return null;
        }

        try {
            String[] parts = storedId.split(":");
            if (parts.length == 2) {
                return new NamespacedKey(parts[0], parts[1]);
            }
        } catch (Exception e) {
            // Invalid stored ID
        }

        return null;
    }

    @Override
    public String toString() {
        return "Item{" + id + ", " + displayName + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ItemDefinition other)) return false;
        return id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
