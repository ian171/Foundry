package cn.epicmc.foundry.api.annotations;

import java.lang.annotation.*;

/**
 * Marks a class as a Foundry content type and provides its unique identifier.
 * <p>
 * This is the fundamental annotation that all Foundry content must have.
 * It provides the namespace and ID that will be used to register the content
 * in the appropriate registry.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "copper_generator")
 * public class CopperGenerator extends AbstractBlock {
 *     // ...
 * }
 * }</pre>
 * <p>
 * <b>Important:</b> This annotation is declarative syntax sugar.
 * The actual registration still happens through the Registry system during
 * the REGISTRY phase. Annotation processors or registration handlers scan
 * for these annotations and call the appropriate registry methods.
 *
 * @see cn.epicmc.foundry.api.registry.Registry
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Foundry {

    /**
     * The namespace (typically the plugin name).
     * <p>
     * Must be lowercase alphanumeric with underscores only.
     * Example: {@code "example"}, {@code "my_plugin"}
     *
     * @return The namespace
     */
    String namespace();

    /**
     * The unique identifier within the namespace.
     * <p>
     * Must be lowercase alphanumeric with underscores only.
     * Example: {@code "copper_generator"}, {@code "electric_furnace"}
     *
     * @return The unique ID
     */
    String id();

    /**
     * Optional display name.
     * <p>
     * If not provided, will be generated from the ID.
     * Example: {@code "Copper Generator"}, {@code "Electric Furnace"}
     *
     * @return The display name, or empty to auto-generate
     */
    String name() default "";

    /**
     * Optional description.
     *
     * @return The description
     */
    String description() default "";
}
