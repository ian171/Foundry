package cn.epicmc.foundry.api.annotations;

import org.bukkit.Material;

import java.lang.annotation.*;

/**
 * Marks a class as a Foundry item and defines its basic properties.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "copper_ingot")
 * @FoundryItem(
 *     material = Material.IRON_INGOT,
 *     customModelData = 100001,
 *     maxStackSize = 64
 * )
 * public class CopperIngot extends AbstractItem {
 *     // ...
 * }
 * }</pre>
 *
 * @see Foundry
 * @see cn.epicmc.foundry.api.item.ItemDefinition
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FoundryItem {

    /**
     * The base Minecraft material to use for this item.
     *
     * @return The base material
     */
    Material material();

    /**
     * Custom model data for resource pack models.
     * <p>
     * Set to 0 to not use custom models.
     *
     * @return The custom model data value
     */
    int customModelData() default 0;

    /**
     * Maximum stack size for this item.
     * <p>
     * Valid range: 1-64
     *
     * @return The maximum stack size
     */
    int maxStackSize() default 64;

    /**
     * Lore lines to display in the item tooltip.
     *
     * @return The lore lines
     */
    String[] lore() default {};

    /**
     * The block this item places, if any.
     * <p>
     * Format: {@code "namespace:id"}
     * Example: {@code "example:copper_generator"}
     * <p>
     * If empty, this is not a block item.
     *
     * @return The block ID, or empty
     */
    String placesBlock() default "";
}
