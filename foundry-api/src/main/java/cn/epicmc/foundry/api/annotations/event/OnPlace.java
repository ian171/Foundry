package cn.epicmc.foundry.api.annotations.event;

import java.lang.annotation.*;

/**
 * Marks a method to be called when a Foundry block is placed.
 * <p>
 * The method must have the signature:
 * <pre>{@code
 * public void onPlace(PlacementContext context)
 * }</pre>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "generator")
 * @FoundryBlock(material = Material.NOTE_BLOCK)
 * public class Generator extends AbstractBlock {
 *
 *     @OnPlace
 *     public void onPlace(PlacementContext context) {
 *         context.getPlayer().ifPresent(player -> {
 *             player.sendMessage("Generator placed!");
 *         });
 *     }
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnPlace {
}
