package cn.epicmc.foundry.api.capability;

import org.bukkit.NamespacedKey;

import java.util.Objects;

/**
 * A typed key that identifies a specific capability type.
 * <p>
 * Capability keys are used to query whether a block, machine, or entity
 * supports a particular capability (energy, fluids, items, etc).
 * <p>
 * <b>Example Usage:</b>
 * <pre>{@code
 * // Define a capability key
 * public static final CapabilityKey<EnergyHandler> ENERGY =
 *     CapabilityKey.of("foundry", "energy", EnergyHandler.class);
 *
 * // Query a capability
 * Optional<EnergyHandler> handler = provider.getCapability(ENERGY, Direction.UP);
 * }</pre>
 *
 * @param <T> The type of capability interface
 * @see Capability
 * @see CapabilityProvider
 */
public final class CapabilityKey<T> {

    private final NamespacedKey id;
    private final Class<T> type;

    private CapabilityKey(NamespacedKey id, Class<T> type) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
    }

    /**
     * Create a new capability key.
     *
     * @param namespace The namespace (typically the plugin name)
     * @param key The capability name
     * @param type The capability interface class
     * @param <T> The capability type
     * @return A new capability key
     */
    public static <T> CapabilityKey<T> of(String namespace, String key, Class<T> type) {
        return new CapabilityKey<>(new NamespacedKey(namespace, key), type);
    }

    /**
     * Create a new capability key from a NamespacedKey.
     *
     * @param id The namespaced key
     * @param type The capability interface class
     * @param <T> The capability type
     * @return A new capability key
     */
    public static <T> CapabilityKey<T> of(NamespacedKey id, Class<T> type) {
        return new CapabilityKey<>(id, type);
    }

    /**
     * Get the unique identifier of this capability.
     *
     * @return The namespaced key
     */
    public NamespacedKey getId() {
        return id;
    }

    /**
     * Get the type of interface this capability represents.
     *
     * @return The capability class
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Check if this capability key matches the given class.
     * <p>
     * This checks if the capability type is assignable from the given class.
     *
     * @param clazz The class to check
     * @return {@code true} if compatible, {@code false} otherwise
     */
    public boolean isAssignableFrom(Class<?> clazz) {
        return type.isAssignableFrom(clazz);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CapabilityKey<?> other)) return false;
        return id.equals(other.id) && type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return 31 * id.hashCode() + type.hashCode();
    }

    @Override
    public String toString() {
        return "CapabilityKey{" + id + ", type=" + type.getSimpleName() + "}";
    }
}
