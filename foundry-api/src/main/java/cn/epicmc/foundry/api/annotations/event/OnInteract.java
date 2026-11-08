package cn.epicmc.foundry.api.annotations.event;

import java.lang.annotation.*;

/**
 * Marks a method to be called when a player interacts with a Foundry block.
 * <p>
 * The method should have the signature:
 * <pre>{@code
 * public boolean onInteract(Player player, Action action)
 * }</pre>
 * <p>
 * Return {@code true} to cancel the interaction event.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "generator")
 * @FoundryBlock(material = Material.NOTE_BLOCK, isMachine = true)
 * public class Generator extends AbstractBlock {
 *
 *     @OnInteract
 *     public boolean onInteract(Player player, Action action) {
 *         if (action == Action.RIGHT_CLICK_BLOCK) {
 *             // Open GUI
 *             openGUI(player);
 *             return true; // Cancel event
 *         }
 *         return false;
 *     }
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnInteract {
}
