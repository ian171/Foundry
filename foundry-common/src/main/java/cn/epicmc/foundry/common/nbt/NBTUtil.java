package cn.epicmc.foundry.common.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Utility class for type-safe NBT operations with automatic namespace management.
 * <p>
 * All keys are automatically prefixed with "foundry:" namespace to prevent conflicts
 * with other plugins.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Save
 * NBTUtil.setDouble(container, "energy", 1000.0);
 * NBTUtil.setInt(container, "progress", 50);
 * NBTUtil.setString(container, "owner", uuid.toString());
 *
 * // Load with defaults
 * double energy = NBTUtil.getDouble(container, "energy", 0.0);
 * int progress = NBTUtil.getInt(container, "progress", 0);
 * String owner = NBTUtil.getString(container, "owner", "");
 * }</pre>
 *
 * @see NBTSerializable
 */
public final class NBTUtil {

    private static final String NAMESPACE = "foundry";

    private NBTUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Create a namespaced key with the foundry namespace.
     *
     * @param key The key name
     * @return A namespaced key "foundry:key"
     */
    public static NamespacedKey key(String key) {
        return new NamespacedKey(NAMESPACE, key);
    }

    // ==================== Primitive Types ====================

    /**
     * Set a byte value.
     */
    public static void setByte(PersistentDataContainer container, String key, byte value) {
        container.set(key(key), PersistentDataType.BYTE, value);
    }

    /**
     * Get a byte value with default.
     */
    public static byte getByte(PersistentDataContainer container, String key, byte defaultValue) {
        Byte value = container.get(key(key), PersistentDataType.BYTE);
        return value != null ? value : defaultValue;
    }

    /**
     * Set a short value.
     */
    public static void setShort(PersistentDataContainer container, String key, short value) {
        container.set(key(key), PersistentDataType.SHORT, value);
    }

    /**
     * Get a short value with default.
     */
    public static short getShort(PersistentDataContainer container, String key, short defaultValue) {
        Short value = container.get(key(key), PersistentDataType.SHORT);
        return value != null ? value : defaultValue;
    }

    /**
     * Set an int value.
     */
    public static void setInt(PersistentDataContainer container, String key, int value) {
        container.set(key(key), PersistentDataType.INTEGER, value);
    }

    /**
     * Get an int value with default.
     */
    public static int getInt(PersistentDataContainer container, String key, int defaultValue) {
        Integer value = container.get(key(key), PersistentDataType.INTEGER);
        return value != null ? value : defaultValue;
    }

    /**
     * Set a long value.
     */
    public static void setLong(PersistentDataContainer container, String key, long value) {
        container.set(key(key), PersistentDataType.LONG, value);
    }

    /**
     * Get a long value with default.
     */
    public static long getLong(PersistentDataContainer container, String key, long defaultValue) {
        Long value = container.get(key(key), PersistentDataType.LONG);
        return value != null ? value : defaultValue;
    }

    /**
     * Set a float value.
     */
    public static void setFloat(PersistentDataContainer container, String key, float value) {
        container.set(key(key), PersistentDataType.FLOAT, value);
    }

    /**
     * Get a float value with default.
     */
    public static float getFloat(PersistentDataContainer container, String key, float defaultValue) {
        Float value = container.get(key(key), PersistentDataType.FLOAT);
        return value != null ? value : defaultValue;
    }

    /**
     * Set a double value.
     */
    public static void setDouble(PersistentDataContainer container, String key, double value) {
        container.set(key(key), PersistentDataType.DOUBLE, value);
    }

    /**
     * Get a double value with default.
     */
    public static double getDouble(PersistentDataContainer container, String key, double defaultValue) {
        Double value = container.get(key(key), PersistentDataType.DOUBLE);
        return value != null ? value : defaultValue;
    }

    /**
     * Set a string value.
     */
    public static void setString(PersistentDataContainer container, String key, String value) {
        if (value == null) {
            container.remove(key(key));
        } else {
            container.set(key(key), PersistentDataType.STRING, value);
        }
    }

    /**
     * Get a string value with default.
     */
    public static String getString(PersistentDataContainer container, String key, String defaultValue) {
        String value = container.get(key(key), PersistentDataType.STRING);
        return value != null ? value : defaultValue;
    }

    /**
     * Set a boolean value (stored as byte: 0 = false, 1 = true).
     */
    public static void setBoolean(PersistentDataContainer container, String key, boolean value) {
        container.set(key(key), PersistentDataType.BYTE, value ? (byte) 1 : (byte) 0);
    }

    /**
     * Get a boolean value with default.
     */
    public static boolean getBoolean(PersistentDataContainer container, String key, boolean defaultValue) {
        Byte value = container.get(key(key), PersistentDataType.BYTE);
        return value != null ? value != 0 : defaultValue;
    }

    // ==================== Arrays ====================

    /**
     * Set a byte array.
     */
    public static void setByteArray(PersistentDataContainer container, String key, byte[] value) {
        if (value == null) {
            container.remove(key(key));
        } else {
            container.set(key(key), PersistentDataType.BYTE_ARRAY, value);
        }
    }

    /**
     * Get a byte array with default.
     */
    public static byte[] getByteArray(PersistentDataContainer container, String key, byte[] defaultValue) {
        byte[] value = container.get(key(key), PersistentDataType.BYTE_ARRAY);
        return value != null ? value : defaultValue;
    }

    /**
     * Set an int array.
     */
    public static void setIntArray(PersistentDataContainer container, String key, int[] value) {
        if (value == null) {
            container.remove(key(key));
        } else {
            container.set(key(key), PersistentDataType.INTEGER_ARRAY, value);
        }
    }

    /**
     * Get an int array with default.
     */
    public static int[] getIntArray(PersistentDataContainer container, String key, int[] defaultValue) {
        int[] value = container.get(key(key), PersistentDataType.INTEGER_ARRAY);
        return value != null ? value : defaultValue;
    }

    /**
     * Set a long array.
     */
    public static void setLongArray(PersistentDataContainer container, String key, long[] value) {
        if (value == null) {
            container.remove(key(key));
        } else {
            container.set(key(key), PersistentDataType.LONG_ARRAY, value);
        }
    }

    /**
     * Get a long array with default.
     */
    public static long[] getLongArray(PersistentDataContainer container, String key, long[] defaultValue) {
        long[] value = container.get(key(key), PersistentDataType.LONG_ARRAY);
        return value != null ? value : defaultValue;
    }

    // ==================== Complex Types ====================

    /**
     * Set a UUID value.
     */
    public static void setUUID(PersistentDataContainer container, String key, UUID value) {
        if (value == null) {
            container.remove(key(key));
        } else {
            setString(container, key, value.toString());
        }
    }

    /**
     * Get a UUID value with default.
     */
    public static UUID getUUID(PersistentDataContainer container, String key, UUID defaultValue) {
        String str = getString(container, key, null);
        if (str == null) {
            return defaultValue;
        }
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    /**
     * Set an enum value.
     */
    public static <E extends Enum<E>> void setEnum(PersistentDataContainer container, String key, E value) {
        if (value == null) {
            container.remove(key(key));
        } else {
            setString(container, key, value.name());
        }
    }

    /**
     * Get an enum value with default.
     */
    public static <E extends Enum<E>> E getEnum(PersistentDataContainer container, String key, Class<E> enumClass, E defaultValue) {
        String str = getString(container, key, null);
        if (str == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, str);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    // ==================== Nested Containers ====================

    /**
     * Save a nested NBTSerializable object.
     */
    public static void saveNested(PersistentDataContainer container, String key, NBTSerializable object) {
        if (object == null) {
            container.remove(key(key));
        } else {
            PersistentDataContainer nested = container.getAdapterContext().newPersistentDataContainer();
            object.save(nested);
            container.set(key(key), PersistentDataType.TAG_CONTAINER, nested);
        }
    }

    /**
     * Load a nested NBTSerializable object.
     */
    public static void loadNested(PersistentDataContainer container, String key, NBTSerializable object) {
        PersistentDataContainer nested = container.get(key(key), PersistentDataType.TAG_CONTAINER);
        if (nested != null && object != null) {
            object.load(nested);
        }
    }

    /**
     * Get or create a nested container.
     */
    public static PersistentDataContainer getOrCreateContainer(PersistentDataContainer container, String key) {
        PersistentDataContainer nested = container.get(key(key), PersistentDataType.TAG_CONTAINER);
        if (nested == null) {
            nested = container.getAdapterContext().newPersistentDataContainer();
            container.set(key(key), PersistentDataType.TAG_CONTAINER, nested);
        }
        return nested;
    }

    // ==================== Collections ====================

    /**
     * Save a list of strings.
     */
    public static void setStringList(PersistentDataContainer container, String key, List<String> list) {
        if (list == null || list.isEmpty()) {
            container.remove(key(key));
        } else {
            setInt(container, key + "_size", list.size());
            for (int i = 0; i < list.size(); i++) {
                setString(container, key + "_" + i, list.get(i));
            }
        }
    }

    /**
     * Load a list of strings.
     */
    public static List<String> getStringList(PersistentDataContainer container, String key) {
        int size = getInt(container, key + "_size", 0);
        if (size == 0) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String value = getString(container, key + "_" + i, null);
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }

    // ==================== Utility Methods ====================

    /**
     * Check if a key exists in the container.
     */
    public static boolean has(PersistentDataContainer container, String key) {
        return container.has(key(key));
    }

    /**
     * Remove a key from the container.
     */
    public static void remove(PersistentDataContainer container, String key) {
        container.remove(key(key));
    }

    /**
     * Clear all foundry keys from the container.
     */
    public static void clear(PersistentDataContainer container) {
        Set<NamespacedKey> keys = container.getKeys();
        keys.stream()
            .filter(k -> NAMESPACE.equals(k.getNamespace()))
            .forEach(container::remove);
    }

    /**
     * Copy all foundry keys from source to destination.
     */
    public static void copyAll(PersistentDataContainer source, PersistentDataContainer destination) {
        Set<NamespacedKey> keys = source.getKeys();
        for (NamespacedKey key : keys) {
            if (NAMESPACE.equals(key.getNamespace())) {
                // Copy using TAG_CONTAINER to preserve type
                PersistentDataContainer temp = source.getAdapterContext().newPersistentDataContainer();
                source.copyTo(temp, true);
                destination.copyTo(temp, false);
            }
        }
    }
}
