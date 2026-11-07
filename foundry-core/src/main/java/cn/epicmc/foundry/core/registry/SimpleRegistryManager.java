package cn.epicmc.foundry.core.registry;

import cn.epicmc.foundry.api.registry.*;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Default implementation of the registry manager.
 * <p>
 * This manages all registries and controls their lifecycle through phases.
 */
public class SimpleRegistryManager implements RegistryManager {

    private final Plugin plugin;
    private final Logger logger;
    private final Map<RegistryKey<?>, MutableRegistry<?>> registries;

    private volatile RegistrationPhase currentPhase;

    /**
     * Create a new registry manager.
     *
     * @param plugin The plugin instance
     */
    public SimpleRegistryManager(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.logger = plugin.getLogger();
        this.registries = new ConcurrentHashMap<>();
        this.currentPhase = RegistrationPhase.BOOTSTRAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Registry<T> getRegistry(RegistryKey<T> key) {
        Objects.requireNonNull(key, "key");

        MutableRegistry<?> registry = registries.get(key);
        if (registry == null) {
            throw new IllegalArgumentException("Registry not found: " + key);
        }

        return (Registry<T>) registry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> MutableRegistry<T> getMutableRegistry(RegistryKey<T> key) {
        Objects.requireNonNull(key, "key");

        MutableRegistry<?> registry = registries.get(key);
        if (registry == null) {
            throw new IllegalArgumentException("Registry not found: " + key);
        }

        // Check if we can modify
        if (!registry.isOpen()) {
            throw new IllegalStateException(
                "Cannot get mutable registry in " + currentPhase +
                " phase: " + key
            );
        }

        return (MutableRegistry<T>) registry;
    }

    @Override
    public Collection<RegistryKey<?>> getRegistryKeys() {
        return Collections.unmodifiableSet(registries.keySet());
    }

    @Override
    public boolean hasRegistry(RegistryKey<?> key) {
        return registries.containsKey(key);
    }

    @Override
    public RegistrationPhase getCurrentPhase() {
        return currentPhase;
    }

    @Override
    public synchronized void transitionTo(RegistrationPhase phase) {
        Objects.requireNonNull(phase, "phase");

        // Validate phase transition
        if (phase.isBefore(currentPhase)) {
            throw new IllegalStateException(
                "Cannot go backwards from " + currentPhase + " to " + phase
            );
        }

        if (phase.ordinal() > currentPhase.ordinal() + 1) {
            throw new IllegalStateException(
                "Cannot skip phases from " + currentPhase + " to " + phase
            );
        }

        logger.info("Transitioning from " + currentPhase + " to " + phase);

        // Perform phase transition actions
        switch (phase) {
            case WORLD_LOAD:
                freezeStaticRegistries();
                break;
            case RUNTIME:
                logRegistryStatistics();
                break;
            case SHUTDOWN:
                // Cleanup will be handled separately
                break;
        }

        this.currentPhase = phase;
        logger.info("Now in " + phase + " phase");
    }

    /**
     * Create and register a new registry.
     * <p>
     * This should be called during BOOTSTRAP phase to set up core registries.
     *
     * @param key The registry key
     * @param supportsDynamic Whether the registry supports dynamic registration
     * @param <T> The type parameter
     * @return The created registry
     */
    public <T> MutableRegistry<T> createRegistry(
        RegistryKey<T> key,
        boolean supportsDynamic
    ) {
        Objects.requireNonNull(key, "key");

        if (currentPhase != RegistrationPhase.BOOTSTRAP) {
            throw new IllegalStateException(
                "Registries can only be created during BOOTSTRAP phase"
            );
        }

        if (registries.containsKey(key)) {
            throw new IllegalArgumentException("Registry already exists: " + key);
        }

        SimpleRegistry<T> registry = new SimpleRegistry<>(key, supportsDynamic);
        registries.put(key, registry);

        logger.info("Created registry: " + key +
                   (supportsDynamic ? " (dynamic)" : " (static)"));

        return registry;
    }

    /**
     * Freeze all static registries.
     * <p>
     * Called when entering WORLD_LOAD phase.
     */
    private void freezeStaticRegistries() {
        logger.info("Freezing static registries...");

        int frozenCount = 0;
        for (MutableRegistry<?> registry : registries.values()) {
            if (!registry.supportsDynamicRegistration()) {
                registry.freeze();
                frozenCount++;
            }
        }

        logger.info("Frozen " + frozenCount + " static registries");
    }

    /**
     * Log statistics about all registries.
     */
    private void logRegistryStatistics() {
        logger.info("Registry Statistics:");

        for (Map.Entry<RegistryKey<?>, MutableRegistry<?>> entry : registries.entrySet()) {
            RegistryKey<?> key = entry.getKey();
            MutableRegistry<?> registry = entry.getValue();

            logger.info("  " + key.getKey() + ": " +
                       registry.size() + " entries" +
                       (registry.supportsDynamicRegistration() ? " (dynamic)" : ""));
        }
    }
}
