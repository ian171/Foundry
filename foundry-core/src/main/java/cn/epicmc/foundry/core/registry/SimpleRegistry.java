package cn.epicmc.foundry.core.registry;

import cn.epicmc.foundry.api.registry.*;
import org.bukkit.NamespacedKey;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of a registry.
 * <p>
 * This implementation uses a {@link ConcurrentHashMap} for thread-safe read operations.
 * Write operations (registration) are synchronized to prevent concurrent modification.
 *
 * @param <T> The type of objects stored in this registry
 */
public class SimpleRegistry<T> implements MutableRegistry<T> {

    private final RegistryKey<T> registryKey;
    private final Map<NamespacedKey, T> entries;
    private final Map<T, NamespacedKey> reverseIndex;
    private final boolean supportsDynamic;

    private volatile boolean frozen;

    /**
     * Create a new simple registry.
     *
     * @param registryKey The key identifying this registry
     * @param supportsDynamic Whether this registry supports dynamic registration
     */
    public SimpleRegistry(RegistryKey<T> registryKey, boolean supportsDynamic) {
        this.registryKey = Objects.requireNonNull(registryKey, "registryKey");
        this.entries = new ConcurrentHashMap<>();
        this.reverseIndex = new ConcurrentHashMap<>();
        this.supportsDynamic = supportsDynamic;
        this.frozen = false;
    }

    /**
     * Create a new simple registry that does not support dynamic registration.
     *
     * @param registryKey The key identifying this registry
     */
    public SimpleRegistry(RegistryKey<T> registryKey) {
        this(registryKey, false);
    }

    @Override
    public synchronized void register(NamespacedKey key, T value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");

        // Check if frozen
        if (frozen && !supportsDynamic) {
            throw new RegistryFrozenException(registryKey, key);
        }

        // Check for duplicates
        if (entries.containsKey(key)) {
            throw new RegistrationException(
                "Key already registered in " + registryKey + ": " + key,
                key
            );
        }

        // Check if value is already registered
        if (reverseIndex.containsKey(value)) {
            NamespacedKey existingKey = reverseIndex.get(value);
            throw new RegistrationException(
                "Value already registered under key " + existingKey +
                " in " + registryKey,
                key
            );
        }

        // Register
        entries.put(key, value);
        reverseIndex.put(value, key);
    }

    @Override
    public Optional<T> get(NamespacedKey key) {
        Objects.requireNonNull(key, "key");
        return Optional.ofNullable(entries.get(key));
    }

    @Override
    public Collection<T> values() {
        return Collections.unmodifiableCollection(entries.values());
    }

    @Override
    public Collection<NamespacedKey> keys() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    @Override
    public boolean contains(NamespacedKey key) {
        Objects.requireNonNull(key, "key");
        return entries.containsKey(key);
    }

    @Override
    public Optional<NamespacedKey> getKey(T value) {
        Objects.requireNonNull(value, "value");
        return Optional.ofNullable(reverseIndex.get(value));
    }

    @Override
    public RegistryKey<T> getRegistryKey() {
        return registryKey;
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isOpen() {
        return !frozen || supportsDynamic;
    }

    @Override
    public synchronized void freeze() {
        this.frozen = true;
    }

    @Override
    public boolean supportsDynamicRegistration() {
        return supportsDynamic;
    }

    @Override
    public String toString() {
        return "SimpleRegistry{" +
               "key=" + registryKey +
               ", size=" + size() +
               ", frozen=" + frozen +
               ", dynamic=" + supportsDynamic +
               '}';
    }
}
