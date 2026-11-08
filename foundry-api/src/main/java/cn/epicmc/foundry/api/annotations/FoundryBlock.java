package cn.epicmc.foundry.api.annotations;

import org.bukkit.Material;

import java.lang.annotation.*;

/**
 * Marks a class as a Foundry block and defines its basic properties.
 * <p>
 * This annotation describes the static, type-level properties of a block.
 * Complex behaviors (interactions, state changes, etc.) should be implemented
 * through Capabilities or dedicated behavior components.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "copper_generator")
 * @FoundryBlock(
 *     material = Material.NOTE_BLOCK,
 *     hardness = 5.0f,
 *     resistance = 6.0f,
 *     isMachine = true
 * )
 * public class CopperGenerator extends AbstractBlock {
 *     // ...
 * }
 * }</pre>
 *
 * @see Foundry
 * @see cn.epicmc.foundry.api.block.BlockDefinition
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FoundryBlock {

    /**
     * The base Minecraft material to use for this block.
     * <p>
     * Common choices:
     * <ul>
     *   <li>{@link Material#NOTE_BLOCK} - Supports custom model data</li>
     *   <li>{@link Material#BARRIER} - Invisible block</li>
     *   <li>{@link Material#MUSHROOM_STEM} - Directional</li>
     * </ul>
     *
     * @return The base material
     */
    Material material();

    /**
     * The hardness of this block.
     * <p>
     * Determines how long it takes to break.
     * Values: Stone=1.5, Iron=5.0, Obsidian=50.0, Bedrock=-1.0
     *
     * @return The hardness value
     */
    float hardness() default 5.0f;

    /**
     * The blast resistance of this block.
     * <p>
     * Determines resistance to explosions.
     * Values: Stone=6.0, Iron=6.0, Obsidian=1200.0
     *
     * @return The blast resistance value
     */
    float resistance() default 6.0f;

    /**
     * Whether this block requires a tool to break efficiently.
     *
     * @return {@code true} if tool required
     */
    boolean requiresTool() default true;

    /**
     * Whether this is a machine (has capabilities, ticks, etc).
     *
     * @return {@code true} if this is a machine
     */
    boolean isMachine() default false;
}
