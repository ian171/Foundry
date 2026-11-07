package cn.epicmc.foundry.api.registry;

import org.bukkit.NamespacedKey;

/**
 * Thrown when attempting to register to a frozen registry.
 * <p>
 * Registries are frozen after the REGISTRY phase ends. Static registration
 * is no longer allowed unless the registry explicitly supports dynamic registration.
 *
 * @see MutableRegistry#freeze()
 * @see RegistrationPhase#RUNTIME
 */
public class RegistryFrozenException extends RegistrationException {

    /**
     * Create a new registry frozen exception.
     *
     * @param registryKey The key of the frozen registry
     */
    public RegistryFrozenException(RegistryKey<?> registryKey) {
        super("Registry is frozen: " + registryKey);
    }

    /**
     * Create a new registry frozen exception with an associated key.
     *
     * @param registryKey The key of the frozen registry
     * @param attemptedKey The key that was attempted to register
     */
    public RegistryFrozenException(RegistryKey<?> registryKey, NamespacedKey attemptedKey) {
        super("Registry is frozen: " + registryKey, attemptedKey);
    }
}
