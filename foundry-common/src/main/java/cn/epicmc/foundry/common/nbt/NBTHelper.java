package cn.epicmc.foundry.common.nbt;

import com.google.common.base.Supplier;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for common NBT serialization patterns.
 * <p>
 * Provides high-level methods for serializing complex game objects
 * that are commonly used in machines and blocks.
 */
public final class NBTHelper {

    private NBTHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== Location Serialization ====================

    /**
     * Save a location to a nested container.
     *
     * @param container Parent container
     * @param key Key for the nested location data
     * @param location Location to save
     */
    public static void saveLocation(PersistentDataContainer container, String key, Location location) {
        if (location == null) {
            NBTUtil.remove(container, key);
            return;
        }

        PersistentDataContainer locData = NBTUtil.getOrCreateContainer(container, key);

        if (location.getWorld() != null) {
            NBTUtil.setString(locData, "world", location.getWorld().getUID().toString());
        }
        NBTUtil.setDouble(locData, "x", location.getX());
        NBTUtil.setDouble(locData, "y", location.getY());
        NBTUtil.setDouble(locData, "z", location.getZ());
        NBTUtil.setFloat(locData, "yaw", location.getYaw());
        NBTUtil.setFloat(locData, "pitch", location.getPitch());
    }

    /**
     * Load a location from a nested container.
     *
     * @param container Parent container
     * @param key Key for the nested location data
     * @return The loaded location, or null if not found
     */
    public static Location loadLocation(PersistentDataContainer container, String key) {
        if (!NBTUtil.has(container, key)) {
            return null;
        }

        PersistentDataContainer locData = NBTUtil.getOrCreateContainer(container, key);

        String worldUUID = NBTUtil.getString(locData, "world", null);
        org.bukkit.World world = null;
        if (worldUUID != null) {
            try {
                world = org.bukkit.Bukkit.getWorld(java.util.UUID.fromString(worldUUID));
            } catch (IllegalArgumentException e) {
                // Invalid UUID
            }
        }

        double x = NBTUtil.getDouble(locData, "x", 0.0);
        double y = NBTUtil.getDouble(locData, "y", 0.0);
        double z = NBTUtil.getDouble(locData, "z", 0.0);
        float yaw = NBTUtil.getFloat(locData, "yaw", 0.0f);
        float pitch = NBTUtil.getFloat(locData, "pitch", 0.0f);

        return new Location(world, x, y, z, yaw, pitch);
    }

    // ==================== NamespacedKey Serialization ====================

    /**
     * Save a NamespacedKey.
     *
     * @param container Container to save into
     * @param key Key to save under
     * @param namespacedKey The NamespacedKey to save
     */
    public static void saveNamespacedKey(
        PersistentDataContainer container,
        String key,
        NamespacedKey namespacedKey
    ) {
        if (namespacedKey == null) {
            NBTUtil.remove(container, key + "_namespace");
            NBTUtil.remove(container, key + "_key");
            return;
        }

        NBTUtil.setString(container, key + "_namespace", namespacedKey.getNamespace());
        NBTUtil.setString(container, key + "_key", namespacedKey.getKey());
    }

    /**
     * Load a NamespacedKey.
     *
     * @param container Container to load from
     * @param key Key to load from
     * @param defaultValue Default value if not found
     * @return The loaded NamespacedKey, or defaultValue if not found
     */
    public static org.bukkit.NamespacedKey loadNamespacedKey(
        PersistentDataContainer container,
        String key,
        NamespacedKey defaultValue
    ) {
        String namespace = NBTUtil.getString(container, key + "_namespace", null);
        String keyPart = NBTUtil.getString(container, key + "_key", null);

        if (namespace == null || keyPart == null) {
            return defaultValue;
        }

        try {
            return new NamespacedKey(namespace, keyPart);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    // ==================== ItemStack Serialization ====================

    /**
     * Save an ItemStack using Bukkit's serialization.
     *
     * @param container Container to save into
     * @param key Key to save under
     * @param itemStack The ItemStack to save
     */
    public static void saveItemStack(
        PersistentDataContainer container,
        String key,
        ItemStack itemStack
    ) {
        if (itemStack == null || itemStack.getType().isAir()) {
            NBTUtil.remove(container, key);
            return;
        }

        // Use Bukkit's item serialization
        byte[] serialized = itemStack.serializeAsBytes();
        NBTUtil.setByteArray(container, key, serialized);
    }

    /**
     * Load an ItemStack using Bukkit's deserialization.
     *
     * @param container Container to load from
     * @param key Key to load from
     * @return The loaded ItemStack, or null if not found
     */
    public static ItemStack loadItemStack(
        PersistentDataContainer container,
        String key
    ) {
        byte[] serialized = NBTUtil.getByteArray(container, key, null);
        if (serialized == null) {
            return null;
        }

        try {
            return ItemStack.deserializeBytes(serialized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ==================== Collection Helpers ====================

    /**
     * Save a list of NBTSerializable objects.
     *
     * @param container Container to save into
     * @param key Key prefix for the list
     * @param list List of objects to save
     * @param <T> Type of objects
     */
    public static <T extends NBTSerializable> void saveList(
        PersistentDataContainer container,
        String key,
        List<T> list
    ) {
        if (list == null || list.isEmpty()) {
            NBTUtil.remove(container, key + "_size");
            return;
        }

        NBTUtil.setInt(container, key + "_size", list.size());
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            if (item != null) {
                PersistentDataContainer itemContainer =
                    container.getAdapterContext().newPersistentDataContainer();
                item.save(itemContainer);
                NBTUtil.saveNested(container, key + "_" + i, item);
            }
        }
    }

    /**
     * Load a list of NBTSerializable objects.
     *
     * @param container Container to load from
     * @param key Key prefix for the list
     * @param factory Factory to create new instances
     * @param <T> Type of objects
     * @return The loaded list
     */
    public static <T extends NBTSerializable> java.util.List<T> loadList(
        PersistentDataContainer container,
        String key,
        Supplier<T> factory
    ) {
        int size = NBTUtil.getInt(container, key + "_size", 0);
        if (size == 0) {
            return new java.util.ArrayList<>();
        }

        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            T item = factory.get();
            NBTUtil.loadNested(container, key + "_" + i, item);
            list.add(item);
        }
        return list;
    }
}
