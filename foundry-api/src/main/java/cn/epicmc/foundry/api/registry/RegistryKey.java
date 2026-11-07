package cn.epicmc.foundry.api.registry;

import org.bukkit.NamespacedKey;

/**
 * A typed key that identifies a specific registry.
 * <p>
 * Registry keys are used to look up registries from the {@link RegistryManager}.
 * Each registry type has a unique key.
 * <p>
 * Example usage:
 * <pre>{@code
 * RegistryKey<MachineDefinition> MACHINES =
 *     RegistryKey.of("foundry", "machine_definition", MachineDefinition.class);
 *
 * Registry<MachineDefinition> registry =
 *     RegistryManager.getRegistry(MACHINES);
 * }</pre>
 *
 * @param <T> The type of objects stored in the registry
 */
public final class RegistryKey<T> {

    private final NamespacedKey key;
    private final Class<T> type;

    private RegistryKey(NamespacedKey key, Class<T> type) {
        this.key = key;
        this.type = type;
    }

    /**
     * Create a new registry key.
     *
     * @param namespace The namespace (typically the plugin name)
     * @param key The key (typically the registry type name)
     * @param type The class of objects stored in this registry
     * @param <T> The type parameter
     * @return A new registry key
     */
    public static <T> RegistryKey<T> of(String namespace, String key, Class<T> type) {
        return new RegistryKey<>(new NamespacedKey(namespace, key), type);
    }

    /**
     * Create a new registry key from a NamespacedKey.
     *
     * @param key The namespaced key
     * @param type The class of objects stored in this registry
     * @param <T> The type parameter
     * @return A new registry key
     */
    public static <T> RegistryKey<T> of(NamespacedKey key, Class<T> type) {
        return new RegistryKey<>(key, type);
    }

    /**
     * Get the namespaced key that identifies this registry.
     *
     * @return The namespaced key
     */
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Get the type of objects stored in this registry.
     *
     * @return The object type class
     */
    public Class<T> getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RegistryKey<?> other)) return false;
        return key.equals(other.key) && type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return 31 * key.hashCode() + type.hashCode();
    }

    @Override
    public String toString() {
        return "RegistryKey{" + key + ", type=" + type.getSimpleName() + "}";
    }
}
