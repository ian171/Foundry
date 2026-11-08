package cn.epicmc.foundry.api.annotations;

import cn.epicmc.foundry.api.capability.Capability;

import java.lang.annotation.*;

/**
 * Declares that this machine provides a capability to external accessors.
 * <p>
 * This annotation is declarative - it indicates what capabilities the machine
 * will expose through its {@link cn.epicmc.foundry.api.capability.CapabilityProvider}
 * interface. The actual capability implementation must still be provided in code.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "generator")
 * @FoundryMachine(tickInterval = 20)
 * @ProvideCapability(EnergyHandler.class)
 * public class Generator extends AbstractMachine {
 *
 *     private final EnergyStorage energyStorage =
 *         new SimpleEnergyStorage(10000);
 *
 *     @Override
 *     public <T extends Capability> Optional<T> getCapability(
 *         CapabilityKey<T> key, Direction side
 *     ) {
 *         if (key == FoundryCapabilities.ENERGY) {
 *             return Optional.of(key.getType().cast(energyStorage));
 *         }
 *         return Optional.empty();
 *     }
 * }
 * }</pre>
 *
 * @see RequireCapability
 * @see cn.epicmc.foundry.api.capability.CapabilityProvider
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(ProvideCapabilities.class)
public @interface ProvideCapability {

    /**
     * The capability interface that this machine provides.
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
     * Optional description of how this capability is provided.
     * <p>
     * Used for documentation generation.
     *
     * @return The description
     */
    String description() default "";
}
