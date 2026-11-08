package cn.epicmc.foundry.api.annotations;

import cn.epicmc.foundry.api.capability.Capability;

import java.lang.annotation.*;

/**
 * Declares that this machine requires external objects to provide a capability.
 * <p>
 * This is used to indicate dependencies on capabilities from neighboring blocks,
 * networks, or other systems. The machine's operation may depend on these
 * capabilities being available.
 * <p>
 * <b>Difference from {@link ProvideCapability}:</b>
 * <ul>
 *   <li><b>ProvideCapability:</b> This machine offers the capability to others</li>
 *   <li><b>RequireCapability:</b> This machine needs the capability from others</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "electric_machine")
 * @FoundryMachine(tickInterval = 20)
 * @RequireCapability(value = EnergyHandler.class, optional = false)
 * @ProvideCapability(ItemHandler.class)
 * public class ElectricMachine extends AbstractMachine {
 *
 *     @Override
 *     public void tick() {
 *         // Try to find energy source from neighboring blocks
 *         for (Direction dir : Direction.values()) {
 *             Location neighbor = getLocation().getBlock()
 *                 .getRelative(dir.toBlockFace())
 *                 .getLocation();
 *
 *             Optional<CapabilityProvider> provider =
 *                 blockManager.getBlock(neighbor);
 *
 *             if (provider.isPresent()) {
 *                 Optional<EnergyHandler> energy =
 *                     provider.get().getCapability(
 *                         FoundryCapabilities.ENERGY,
 *                         dir.getOpposite()
 *                     );
 *
 *                 if (energy.isPresent()) {
 *                     // Extract energy from neighbor
 *                     double extracted = energy.get().extract(100, false);
 *                     // Use energy...
 *                 }
 *             }
 *         }
 *     }
 * }
 * }</pre>
 *
 * @see ProvideCapability
 * @see cn.epicmc.foundry.api.capability.CapabilityProvider
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(RequireCapabilities.class)
public @interface RequireCapability {

    /**
     * The capability interface that this machine requires from external sources.
     * <p>
     * This must be an interface that extends {@link Capability}.
     * <p>
     * Examples:
     * <ul>
     *   <li>{@code EnergyHandler.class}</li>
     *   <li>{@code FluidHandler.class}</li>
     *   <li>{@code ItemHandler.class}</li>
     * </ul>
     *
     * @return The capability class
     */
    Class<? extends Capability> value();

    /**
     * Whether this capability is optional.
     * <p>
     * If {@code false}, the machine cannot function without this capability
     * being available from external sources.
     * <p>
     * If {@code true}, the machine can work with reduced functionality
     * when this capability is not available.
     *
     * @return {@code true} if optional, {@code false} if required
     */
    boolean optional() default false;

    /**
     * Optional description of why this capability is required.
     * <p>
     * Used for documentation generation and error messages.
     *
     * @return The description
     */
    String description() default "";
}
