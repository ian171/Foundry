package cn.epicmc.foundry.api.registry;

/**
 * Represents the lifecycle phase of the Foundry framework.
 * <p>
 * The framework progresses through these phases in order. Each phase
 * has specific allowed operations and restrictions.
 * <p>
 * <b>Phase Progression:</b>
 * <pre>
 * Plugin Enable
 *       ↓
 * BOOTSTRAP     - Initialize core services
 *       ↓
 * REGISTRY      - Plugin registration phase (static definitions)
 *       ↓
 * WORLD_LOAD    - Load world data and activate machines
 *       ↓
 * RUNTIME       - Normal operation (registries frozen)
 *       ↓
 * SHUTDOWN      - Save data and cleanup
 * </pre>
 *
 * @see MutableRegistry
 * @see RegistryManager
 */
public enum RegistrationPhase {

    /**
     * Bootstrap phase - core infrastructure initialization.
     * <p>
     * During this phase:
     * <ul>
     *   <li>Core registries are created</li>
     *   <li>Core services are initialized</li>
     *   <li>Platform adapters are loaded</li>
     *   <li>No plugins have loaded yet</li>
     * </ul>
     * <p>
     * <b>Allowed Operations:</b> Internal framework initialization only
     */
    BOOTSTRAP("Bootstrap"),

    /**
     * Registry phase - plugins register static definitions.
     * <p>
     * During this phase:
     * <ul>
     *   <li>Plugins can register machine definitions</li>
     *   <li>Plugins can register energy types</li>
     *   <li>Plugins can register fluid types</li>
     *   <li>Plugins can register recipe types</li>
     *   <li>No world access is available</li>
     *   <li>No machines are instantiated yet</li>
     * </ul>
     * <p>
     * <b>Allowed Operations:</b> Register static definitions
     */
    REGISTRY("Registry"),

    /**
     * World loading phase - load persistent data from worlds.
     * <p>
     * During this phase:
     * <ul>
     *   <li>World data is loaded from database</li>
     *   <li>Machine instances are created</li>
     *   <li>Networks are reconstructed</li>
     *   <li>Registries are frozen (no more static registration)</li>
     * </ul>
     * <p>
     * <b>Allowed Operations:</b> Load and instantiate world objects
     */
    WORLD_LOAD("World Load"),

    /**
     * Runtime phase - normal operation.
     * <p>
     * During this phase:
     * <ul>
     *   <li>Static registries are frozen</li>
     *   <li>Machines are running</li>
     *   <li>Networks are active</li>
     *   <li>Players can interact with machines</li>
     *   <li>Dynamic registration is not allowed (unless explicitly supported)</li>
     * </ul>
     * <p>
     * <b>Allowed Operations:</b> Normal gameplay operations
     */
    RUNTIME("Runtime"),

    /**
     * Shutdown phase - cleanup and data persistence.
     * <p>
     * During this phase:
     * <ul>
     *   <li>Save all machine states</li>
     *   <li>Unload all machines</li>
     *   <li>Close database connections</li>
     *   <li>Cleanup resources</li>
     *   <li>Close all GUIs</li>
     * </ul>
     * <p>
     * <b>Allowed Operations:</b> Save data and cleanup
     */
    SHUTDOWN("Shutdown");

    private final String displayName;

    RegistrationPhase(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the human-readable name of this phase.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if static registration is allowed in this phase.
     * <p>
     * Static registration is only allowed during BOOTSTRAP and REGISTRY phases.
     *
     * @return {@code true} if registration is allowed, {@code false} otherwise
     */
    public boolean allowsRegistration() {
        return this == BOOTSTRAP || this == REGISTRY;
    }

    /**
     * Check if this phase comes before another phase.
     *
     * @param other The phase to compare against
     * @return {@code true} if this phase comes before the other phase
     */
    public boolean isBefore(RegistrationPhase other) {
        return this.ordinal() < other.ordinal();
    }

    /**
     * Check if this phase comes after another phase.
     *
     * @param other The phase to compare against
     * @return {@code true} if this phase comes after the other phase
     */
    public boolean isAfter(RegistrationPhase other) {
        return this.ordinal() > other.ordinal();
    }
}
