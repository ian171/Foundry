package cn.epicmc.foundry.api.annotations;

import java.lang.annotation.*;

/**
 * Container annotation for multiple {@link RequireCapability} annotations.
 *
 * @see RequireCapability
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireCapabilities {

    /**
     * The array of required capabilities.
     *
     * @return The capabilities
     */
    RequireCapability[] value();
}
