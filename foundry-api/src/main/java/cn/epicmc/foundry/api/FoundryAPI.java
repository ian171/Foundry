package cn.epicmc.foundry.api;

import cn.epicmc.foundry.api.registry.RegistryManager;
import com.google.common.annotations.Beta;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Main API entry point for the Foundry framework.
 * <p>
 * This class provides access to all core Foundry systems:
 * <ul>
 *   <li>Registry Manager - Access to all registries</li>
 *   <li>Block Manager - Block instance management</li>
 *   <li>Capability System - Capability queries</li>
 * </ul>
 * <p>
 * <b>Thread Safety:</b> All methods in this class are thread-safe.
 * The initialization uses atomic operations to prevent race conditions.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Get registry manager
 * RegistryManager manager = FoundryAPI.getRegistryManager();
 *
 * // Access a registry
 * Registry<EnergyUnit> energyUnits =
 *     manager.getRegistry(FoundryRegistries.ENERGY_UNITS);
 * }</pre>
 */
@Beta
public final class FoundryAPI {

    /**
     * API version for compatibility checking.
     */
    public static final String API_VERSION = "1.0.0";

    /**
     * Thread-safe holder for the registry manager.
     */
    private static final AtomicReference<RegistryManager> registryManager =
        new AtomicReference<>();

    private FoundryAPI() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get the registry manager.
     * <p>
     * The registry manager provides access to all registries in the framework.
     * <p>
     * <b>Thread Safety:</b> This method is thread-safe and can be called from
     * multiple threads simultaneously.
     *
     * @return The registry manager
     * @throws IllegalStateException if Foundry is not initialized
     */
    public static RegistryManager getRegistryManager() {
        RegistryManager manager = registryManager.get();
        if (manager == null) {
            throw new IllegalStateException(
                "Foundry is not initialized.\n" +
                "Did you access the API before the plugin enabled?"
            );
        }
        return manager;
    }

    /**
     * Initialize the API with a registry manager.
     * <p>
     * <b>Internal use only.</b> This is called by the Foundry core during startup.
     * <p>
     * <b>Thread Safety:</b> This method uses atomic compare-and-set to ensure
     * only one initialization succeeds, even if called from multiple threads.
     *
     * @param manager The registry manager
     * @throws IllegalArgumentException if manager is null
     * @throws IllegalStateException if already initialized
     */
    public static void initialize(RegistryManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Registry manager cannot be null");
        }

        if (!registryManager.compareAndSet(null, manager)) {
            throw new IllegalStateException(
                "Foundry API already initialized.\n" +
                "Attempted to initialize with: " + manager.getClass().getName()
            );
        }
    }

    /**
     * Shutdown the API.
     * <p>
     * <b>Internal use only.</b> This is called by the Foundry core during shutdown.
     * <p>
     * <b>Thread Safety:</b> This method is thread-safe and will atomically
     * clear the registry manager reference.
     */
    public static void shutdown() {
        registryManager.set(null);
    }

    /**
     * Check if the API is initialized.
     * <p>
     * <b>Thread Safety:</b> This method is thread-safe but the result may
     * become stale immediately after return if another thread initializes
     * or shuts down the API concurrently.
     *
     * @return true if initialized
     */
    public static boolean isInitialized() {
        return registryManager.get() != null;
    }

    /**
     * Get the API version.
     * <p>
     * This can be used to check compatibility with the current API.
     *
     * @return The API version string (e.g., "1.0.0")
     */
    public static String getApiVersion() {
        return API_VERSION;
    }
}
