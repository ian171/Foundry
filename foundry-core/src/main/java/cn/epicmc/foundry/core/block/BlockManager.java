package cn.epicmc.foundry.core.block;

import cn.epicmc.foundry.api.block.BlockBinding;
import cn.epicmc.foundry.api.block.BlockInstance;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the mapping between physical block locations and logical block instances.
 * <p>
 * This class maintains bidirectional mappings:
 * <ul>
 *   <li>Location → BlockInstance (for interaction lookups)</li>
 *   <li>UUID → BlockInstance (for state management)</li>
 * </ul>
 * <p>
 * Thread-safe for concurrent access.
 */
public class BlockManager {

    private final Map<LocationKey, BlockBinding> locationBindings;
    private final Map<UUID, BlockInstance> instanceMap;

    public BlockManager() {
        this.locationBindings = new ConcurrentHashMap<>();
        this.instanceMap = new ConcurrentHashMap<>();
    }

    /**
     * Register a block instance at a location.
     *
     * @param instance The block instance
     */
    public void registerBlock(BlockInstance instance) {
        Objects.requireNonNull(instance, "instance");

        LocationKey key = LocationKey.of(instance.getLocation());
        BlockBinding binding = new SimpleBlockBinding(instance);

        locationBindings.put(key, binding);
        instanceMap.put(instance.getUniqueId(), instance);
    }

    /**
     * Unregister a block instance.
     *
     * @param instance The block instance
     */
    public void unregisterBlock(BlockInstance instance) {
        Objects.requireNonNull(instance, "instance");

        LocationKey key = LocationKey.of(instance.getLocation());
        locationBindings.remove(key);
        instanceMap.remove(instance.getUniqueId());
    }

    /**
     * Get the block instance at a location.
     *
     * @param location The location
     * @return The block instance, or empty
     */
    public Optional<BlockInstance> getBlock(Location location) {
        LocationKey key = LocationKey.of(location);
        BlockBinding binding = locationBindings.get(key);

        if (binding == null || !binding.isValid()) {
            return Optional.empty();
        }

        return Optional.ofNullable(instanceMap.get(binding.getInstanceId()));
    }

    /**
     * Get a block instance by its UUID.
     *
     * @param uuid The instance UUID
     * @return The block instance, or empty
     */
    public Optional<BlockInstance> getBlock(UUID uuid) {
        return Optional.ofNullable(instanceMap.get(uuid));
    }

    /**
     * Get all registered block instances.
     *
     * @return Unmodifiable collection of all blocks
     */
    public Collection<BlockInstance> getAllBlocks() {
        return Collections.unmodifiableCollection(instanceMap.values());
    }

    /**
     * Check if a location has a registered block.
     *
     * @param location The location
     * @return true if a block is registered
     */
    public boolean hasBlock(Location location) {
        return getBlock(location).isPresent();
    }

    /**
     * Remove invalid blocks (blocks whose physical block has been destroyed).
     *
     * @return Number of blocks removed
     */
    public int cleanupInvalidBlocks() {
        int removed = 0;

        Iterator<Map.Entry<LocationKey, BlockBinding>> iterator =
            locationBindings.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<LocationKey, BlockBinding> entry = iterator.next();
            BlockBinding binding = entry.getValue();

            if (!binding.isValid()) {
                iterator.remove();
                instanceMap.remove(binding.getInstanceId());
                removed++;
            }
        }

        return removed;
    }

    /**
     * Get the number of registered blocks.
     *
     * @return Block count
     */
    public int getBlockCount() {
        return instanceMap.size();
    }

    /**
     * Clear all registered blocks.
     */
    public void clear() {
        locationBindings.clear();
        instanceMap.clear();
    }

    /**
     * Immutable location key for map lookups.
     */
    private static class LocationKey {
        private final UUID worldUid;
        private final int x, y, z;
        private final int hashCode;

        private LocationKey(UUID worldUid, int x, int y, int z) {
            this.worldUid = worldUid;
            this.x = x;
            this.y = y;
            this.z = z;
            this.hashCode = Objects.hash(worldUid, x, y, z);
        }

        static LocationKey of(Location location) {
            return new LocationKey(
                location.getWorld().getUID(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
            );
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof LocationKey other)) return false;
            return x == other.x && y == other.y && z == other.z &&
                   worldUid.equals(other.worldUid);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    /**
     * Simple implementation of BlockBinding.
     */
    private static class SimpleBlockBinding implements BlockBinding {
        private final Location location;
        private final UUID instanceId;
        private final NamespacedKey definitionId;

        SimpleBlockBinding(BlockInstance instance) {
            this.location = instance.getLocation().clone();
            this.instanceId = instance.getUniqueId();
            this.definitionId = instance.getDefinitionId();
        }

        @Override
        public Location getLocation() {
            return location.clone();
        }

        @Override
        public UUID getInstanceId() {
            return instanceId;
        }

        @Override
        public NamespacedKey getDefinitionId() {
            return definitionId;
        }

        @Override
        public boolean isValid() {
            // Check if physical block still exists
            // This is a basic check - could be enhanced
            return location.getWorld() != null &&
                   location.getWorld().isChunkLoaded(
                       location.getBlockX() >> 4,
                       location.getBlockZ() >> 4
                   );
        }
    }
}
