package cn.epicmc.foundry.api.annotations;

import java.lang.annotation.*;

/**
 * Container annotation for multiple {@link ProvideCapability} annotations.
 * <p>
 * This is automatically used when multiple {@code @ProvideCapability}
 * annotations are present on the same element.
 * <p>
 * <b>Example (automatic):</b>
 * <pre>{@code
 * @ProvideCapability(EnergyHandler.class)
 * @ProvideCapability(FluidHandler.class)
 * @ProvideCapability(ItemHandler.class)
 * public class ProcessingMachine extends AbstractMachine {
 *     // Compiler automatically wraps these in @ProvideCapabilities
 * }
 * }</pre>
 *
 * @see ProvideCapability
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProvideCapabilities {

    /**
     * The array of provided capabilities.
     *
     * @return The capabilities
     */
    ProvideCapability[] value();
}
