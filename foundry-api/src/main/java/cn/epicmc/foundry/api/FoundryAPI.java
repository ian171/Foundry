package cn.epicmc.foundry.api;

import cn.epicmc.foundry.api.registry.RegistryManager;
import com.google.common.annotations.Beta;

import java.lang.annotation.Documented;

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
public final class FoundryAPI {

    private static RegistryManager registryManager;

    private FoundryAPI() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get the registry manager.
     * <p>
     * The registry manager provides access to all registries in the framework.
     *
     * @return The registry manager
     * @throws IllegalStateException if Foundry is not initialized
     */
    public static RegistryManager getRegistryManager() {
        if (registryManager == null) {
            throw new IllegalStateException(
                "Foundry is not initialized. \n" +
                "Did you access the API before the plugin enabled?"
            );
        }
        return registryManager;
    }

    /**
     * Initialize the API with a registry manager.
     * <p>
     * <b>Internal use only.</b> This is called by the Foundry core during startup.
     *
     * @param manager The registry manager
     */
    public static void initialize(RegistryManager manager) {
        if (registryManager != null) {
            throw new IllegalStateException("Foundry API already initialized");
        }
        registryManager = manager;
    }

    /**
     * Shutdown the API.
     * <p>
     * <b>Internal use only.</b> This is called by the Foundry core during shutdown.
     */
    public static void shutdown() {
        registryManager = null;
    }

    /**
     * Check if the API is initialized.
     *
     * @return true if initialized
     */
    public static boolean isInitialized() {
        return registryManager != null;
    }
}
