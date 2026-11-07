package cn.epicmc.foundry.api.registry;

import org.bukkit.NamespacedKey;

/**
 * A registry that allows registration of new entries.
 * <p>
 * Mutable registries can only be modified during specific lifecycle phases.
 * Attempting to register outside the allowed phases will throw an exception.
 * <p>
 * <b>Registration Rules:</b>
 * <ul>
 *   <li>Each key can only be registered once (no overwriting)</li>
 *   <li>Registration conflicts throw {@link RegistrationException}</li>
 *   <li>Registration after freeze throws {@link RegistryFrozenException}</li>
 *   <li>Null keys or values are not allowed</li>
 * </ul>
 *
 * @param <T> The type of objects stored in this registry
 * @see Registry
 * @see RegistrationPhase
 */
public interface MutableRegistry<T> extends Registry<T> {

    /**
     * Register a new object with the given key.
     * <p>
     * This operation is only allowed during the REGISTRY phase or when
     * the registry explicitly supports dynamic registration.
     *
     * @param key The unique identifier for the object
     * @param value The object to register
     * @throws RegistrationException if the key is already registered
     * @throws RegistryFrozenException if the registry is frozen
     * @throws IllegalArgumentException if key or value is null
     * @throws IllegalStateException if called outside the registration phase
     */
    void register(NamespacedKey key, T value);

    /**
     * Check if this registry is currently accepting registrations.
     * <p>
     * A registry is open for registration during:
     * <ul>
     *   <li>BOOTSTRAP phase (for core infrastructure)</li>
     *   <li>REGISTRY phase (for plugin registration)</li>
     *   <li>Anytime, if dynamic registration is supported</li>
     * </ul>
     *
     * @return {@code true} if registration is allowed, {@code false} otherwise
     */
    boolean isOpen();

    /**
     * Freeze this registry, preventing further registration.
     * <p>
     * This is called automatically when entering RUNTIME phase.
     * Once frozen, only registries that support dynamic registration
     * can accept new entries.
     * <p>
     * This operation is idempotent - calling it multiple times has no effect.
     *
     * @see #isOpen()
     */
    void freeze();

    /**
     * Check if this registry supports dynamic registration.
     * <p>
     * Dynamic registries can accept new entries even after being frozen.
     * These registries must explicitly handle unload/cleanup operations.
     * <p>
     * Most registries do NOT support dynamic registration to ensure stability.
     *
     * @return {@code true} if dynamic registration is supported, {@code false} otherwise
     */
    boolean supportsDynamicRegistration();
}
