package cn.epicmc.foundry.api.annotations.event;

import java.lang.annotation.*;

/**
 * Marks a method to be called when a Foundry block is destroyed by an explosion.
 * <p>
 * The method can return {@code boolean} to control whether the explosion
 * should destroy the block:
 * <pre>{@code
 * public boolean onExplode(float yield)
 * }</pre>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "reinforced_chest")
 * @FoundryBlock(material = Material.NOTE_BLOCK, resistance = 1200.0f)
 * public class ReinforcedChest extends AbstractBlock {
 *
 *     @OnExplode
 *     public boolean onExplode(float yield) {
 *         // Drop contents before explosion destroys block
 *         dropContents();
 *         return true; // Allow explosion to destroy block
 *     }
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnExplode {
}
