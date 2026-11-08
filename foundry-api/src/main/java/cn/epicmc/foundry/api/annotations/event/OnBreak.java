package cn.epicmc.foundry.api.annotations.event;

import java.lang.annotation.*;

/**
 * Marks a method to be called when a Foundry block is broken.
 * <p>
 * The method can return {@code boolean} to control whether the break
 * should be cancelled:
 * <pre>{@code
 * public boolean onBreak(Player player)
 * }</pre>
 * <p>
 * Or return void if cancellation is not needed:
 * <pre>{@code
 * public void onBreak(Player player)
 * }</pre>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "generator")
 * @FoundryBlock(material = Material.NOTE_BLOCK)
 * public class Generator extends AbstractBlock {
 *
 *     @OnBreak
 *     public boolean onBreak(Player player) {
 *         if (hasEnergy()) {
 *             player.sendMessage("§cGenerator still has energy!");
 *             return false; // Cancel the break
 *         }
 *         return true; // Allow break
 *     }
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnBreak {
}
