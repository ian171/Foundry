package cn.epicmc.foundry.api.registry;

import java.util.Collection;
import java.util.Optional;

/**
 * Manager for all registries in the Foundry framework.
 * <p>
 * The RegistryManager is the central access point for all registries.
 * It manages the lifecycle of registries and provides type-safe access.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Get a registry
 * Registry<MachineDefinition> registry =
 *     RegistryManager.getRegistry(FoundryRegistries.MACHINES);
 *
 * // Look up a machine
 * Optional<MachineDefinition> machine =
 *     registry.get(new NamespacedKey("example", "generator"));
 * }</pre>
 *
 * @see Registry
 * @see RegistryKey
 */
public interface RegistryManager {

    /**
     * Get a registry by its key.
     * <p>
     * This is the primary method for accessing registries. It returns
     * a read-only view even if the underlying registry is mutable.
     *
     * @param key The registry key
     * @param <T> The type of objects in the registry
     * @return The registry
     * @throws IllegalArgumentException if the registry does not exist
     */
    <T> Registry<T> getRegistry(RegistryKey<T> key);

    /**
     * Get a mutable registry by its key.
     * <p>
     * This method should only be used during registration phases.
     * Attempting to get a mutable registry during RUNTIME will fail
     * unless the registry supports dynamic registration.
     *
     * @param key The registry key
     * @param <T> The type of objects in the registry
     * @return The mutable registry
     * @throws IllegalArgumentException if the registry does not exist
     * @throws IllegalStateException if called outside the registration phase
     */
    <T> MutableRegistry<T> getMutableRegistry(RegistryKey<T> key);

    /**
     * Get all registered registry keys.
     * <p>
     * This returns the keys of all registries managed by this manager.
     *
     * @return An immutable collection of all registry keys
     */
    Collection<RegistryKey<?>> getRegistryKeys();

    /**
     * Check if a registry exists with the given key.
     *
     * @param key The registry key
     * @return {@code true} if the registry exists, {@code false} otherwise
     */
    boolean hasRegistry(RegistryKey<?> key);

    /**
     * Get the current lifecycle phase.
     *
     * @return The current registration phase
     */
    RegistrationPhase getCurrentPhase();

    /**
     * Transition to a new lifecycle phase.
     * <p>
     * <b>Internal API:</b> This method should only be called by the framework core.
     * Plugins must not call this method.
     * <p>
     * Phase transitions must happen in order and cannot go backwards.
     *
     * @param phase The new phase
     * @throws IllegalStateException if trying to go backwards or skip phases
     */
    void transitionTo(RegistrationPhase phase);
}
