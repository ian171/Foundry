package cn.epicmc.foundry.api.registry;

import org.bukkit.NamespacedKey;

import java.util.Collection;
import java.util.Optional;

/**
 * A registry for storing and retrieving objects by their unique {@link NamespacedKey}.
 * <p>
 * Registries are the core infrastructure of the Foundry framework. All extensible
 * objects (machines, fluids, energy types, recipes, etc.) must be registered before use.
 * <p>
 * <b>Thread Safety:</b> Registry implementations should be thread-safe for read operations.
 * Write operations are restricted to specific lifecycle phases.
 * <p>
 * <b>Lifecycle:</b> Registries follow strict lifecycle rules:
 * <ul>
 *   <li><b>REGISTRY Phase:</b> Static definitions can be registered</li>
 *   <li><b>RUNTIME Phase:</b> Static registry is frozen (read-only)</li>
 *   <li><b>Dynamic Registration:</b> Must use special APIs with explicit unload behavior</li>
 * </ul>
 *
 * @param <T> The type of objects stored in this registry
 * @see MutableRegistry
 * @see RegistryKey
 * @see RegistrationPhase
 */
public interface Registry<T> {

    /**
     * Get an object by its unique key.
     * <p>
     * This operation is thread-safe and can be called at any time after
     * the object has been registered.
     *
     * @param key The unique identifier of the object
     * @return An {@link Optional} containing the object if found, or empty if not registered
     * @throws NullPointerException if key is null
     */
    Optional<T> get(NamespacedKey key);

    /**
     * Get all registered values in this registry.
     * <p>
     * The returned collection is immutable and represents a snapshot at the time
     * of the call. Changes to the registry will not be reflected in the collection.
     * <p>
     * The order of elements is unspecified and may change between calls.
     *
     * @return An immutable collection of all registered values
     */
    Collection<T> values();

    /**
     * Get all registered keys in this registry.
     * <p>
     * The returned collection is immutable and represents a snapshot at the time
     * of the call. Changes to the registry will not be reflected in the collection.
     *
     * @return An immutable collection of all registered keys
     */
    Collection<NamespacedKey> keys();

    /**
     * Check if a key is registered in this registry.
     * <p>
     * This is equivalent to {@code get(key).isPresent()} but may be more efficient.
     *
     * @param key The key to check
     * @return {@code true} if the key is registered, {@code false} otherwise
     * @throws NullPointerException if key is null
     */
    boolean contains(NamespacedKey key);

    /**
     * Get the key associated with a registered value.
     * <p>
     * This performs a reverse lookup. If the same object instance is registered
     * under multiple keys, the behavior is unspecified.
     *
     * @param value The value to look up
     * @return An {@link Optional} containing the key if found, or empty if not registered
     * @throws NullPointerException if value is null
     */
    Optional<NamespacedKey> getKey(T value);

    /**
     * Get the registry key that identifies this registry type.
     * <p>
     * Each registry has a unique key that identifies what type of objects it stores.
     * <p>
     * Examples:
     * <ul>
     *   <li>{@code foundry:machine_definition}</li>
     *   <li>{@code foundry:energy_type}</li>
     *   <li>{@code foundry:fluid_type}</li>
     * </ul>
     *
     * @return The registry's unique identifier
     */
    RegistryKey<T> getRegistryKey();

    /**
     * Get the number of entries in this registry.
     *
     * @return The number of registered objects
     */
    int size();

    /**
     * Check if this registry is empty.
     *
     * @return {@code true} if no objects are registered, {@code false} otherwise
     */
    default boolean isEmpty() {
        return size() == 0;
    }
}
