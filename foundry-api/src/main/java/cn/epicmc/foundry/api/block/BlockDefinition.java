package cn.epicmc.foundry.api.block;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

/**
 * Defines a logical block in the Foundry framework.
 * <p>
 * Block definitions represent the static, type-level properties of a block.
 * They are separate from physical Minecraft blocks to allow flexibility in
 * implementation (vanilla blocks, BlockData, resource packs, BlockDisplay entities, etc).
 * <p>
 * <b>Logical vs Physical Separation:</b>
 * <ul>
 *   <li><b>Logical Block:</b> Machine definition, ID, capabilities, behavior</li>
 *   <li><b>Physical Block:</b> Minecraft Material, BlockData, location, visual model</li>
 * </ul>
 * <p>
 * This separation allows:
 * <ul>
 *   <li>Multiple visual representations of the same logical block</li>
 *   <li>Dynamic appearance changes without changing the machine</li>
 *   <li>No dependency on NMS for core functionality</li>
 *   <li>Cross-version compatibility</li>
 * </ul>
 *
 * @see BlockInstance
 * @see BlockBinding
 */
public interface BlockDefinition {

    /**
     * Get the unique identifier of this block definition.
     * <p>
     * This ID is used to register the block in the registry and to
     * look up the definition when loading from storage.
     * <p>
     * Example: {@code example:electric_furnace}
     *
     * @return The unique namespaced key
     */
    NamespacedKey getId();

    /**
     * Get the base material used for the physical block in Minecraft.
     * <p>
     * This is the vanilla Minecraft material that will represent this block
     * in the world. Common choices:
     * <ul>
     *   <li>{@link Material#BARRIER} - Invisible block</li>
     *   <li>{@link Material#NOTE_BLOCK} - Supports custom model data</li>
     *   <li>{@link Material#MUSHROOM_STEM} - Directional, supports custom models</li>
     *   <li>{@link Material#PLAYER_HEAD} - Custom textures via player profiles</li>
     * </ul>
     * <p>
     * <b>Important:</b> The material should not be a block that players can
     * normally obtain or place, to avoid confusion.
     *
     * @return The base Minecraft material
     */
    Material getBaseMaterial();

    /**
     * Get the display name of this block.
     * <p>
     * This is shown to players in tooltips, GUIs, and when hovering over the block.
     * Should be localized if internationalization is supported.
     *
     * @return The human-readable display name
     */
    String getDisplayName();

    /**
     * Check if this block is a machine.
     * <p>
     * Machines have additional behavior:
     * <ul>
     *   <li>They tick and process recipes</li>
     *   <li>They have capabilities (energy, fluids, items)</li>
     *   <li>They have GUIs for player interaction</li>
     *   <li>They store state that must be persisted</li>
     * </ul>
     * <p>
     * Non-machine blocks might be purely decorative or structural.
     *
     * @return {@code true} if this is a machine, {@code false} otherwise
     */
    boolean isMachine();

    /**
     * Get the hardness of this block.
     * <p>
     * Hardness determines how long it takes to break the block.
     * Values are similar to vanilla Minecraft:
     * <ul>
     *   <li>Stone: 1.5</li>
     *   <li>Iron Block: 5.0</li>
     *   <li>Obsidian: 50.0</li>
     *   <li>Bedrock: -1.0 (unbreakable)</li>
     * </ul>
     *
     * @return The hardness value, or -1 for unbreakable blocks
     */
    float getHardness();

    /**
     * Get the blast resistance of this block.
     * <p>
     * Blast resistance determines how well the block resists explosions.
     * Values are similar to vanilla Minecraft:
     * <ul>
     *   <li>Stone: 6.0</li>
     *   <li>Iron Block: 6.0</li>
     *   <li>Obsidian: 1200.0</li>
     *   <li>Bedrock: 3600000.0</li>
     * </ul>
     *
     * @return The blast resistance value
     */
    float getBlastResistance();

    /**
     * Check if this block requires a tool to be broken efficiently.
     *
     * @return {@code true} if a tool is required, {@code false} otherwise
     */
    default boolean requiresTool() {
        return true;
    }

    /**
     * Get the factory for creating instances of this block.
     * <p>
     * The factory is responsible for creating new block/machine instances
     * when the block is placed in the world.
     *
     * @return The block factory, or null if this is not a machine
     */
    BlockFactory getFactory();
}
