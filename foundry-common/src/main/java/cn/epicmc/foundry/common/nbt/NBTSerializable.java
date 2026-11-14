package cn.epicmc.foundry.common.nbt;

import org.bukkit.persistence.PersistentDataContainer;

/**
 * Marker interface for objects that can be serialized to and from NBT data.
 * <p>
 * Objects implementing this interface can be:
 * <ul>
 *   <li>Saved to block entities (via PersistentDataContainer)</li>
 *   <li>Saved to items (via ItemMeta PersistentDataContainer)</li>
 *   <li>Transferred across networks</li>
 *   <li>Stored in databases as serialized NBT</li>
 * </ul>
 * <p>
 * <b>Thread Safety:</b> Implementations should be thread-safe for read operations.
 * Write operations (save/load) should be synchronized if needed.
 * <p>
 * <b>Example Implementation:</b>
 * <pre>{@code
 * public class EnergyStorage implements NBTSerializable {
 *     private double stored;
 *     private final double capacity;
 *
 *     @Override
 *     public void save(PersistentDataContainer container) {
 *         NBTUtil.setDouble(container, "stored", stored);
 *         NBTUtil.setDouble(container, "capacity", capacity);
 *     }
 *
 *     @Override
 *     public void load(PersistentDataContainer container) {
 *         stored = NBTUtil.getDouble(container, "stored", 0.0);
 *         // capacity is final, loaded in constructor
 *     }
 * }
 * }</pre>
 *
 * @see NBTUtil
 * @see PersistentDataContainer
 */
public interface NBTSerializable {

    /**
     * Save this object's state to a PersistentDataContainer.
     * <p>
     * Only save data that needs to persist. Derived or cached values should be
     * recalculated on load.
     * <p>
     * <b>Important:</b> Keys should be namespaced to avoid conflicts.
     * Use {@link NBTUtil} helper methods which automatically handle namespacing.
     *
     * @param container The container to save data into
     */
    void save(PersistentDataContainer container);

    /**
     * Load this object's state from a PersistentDataContainer.
     * <p>
     * This method should be defensive: if data is missing or invalid,
     * use sensible defaults rather than throwing exceptions.
     * <p>
     * <b>Migration:</b> When adding new fields, provide default values for
     * old data that doesn't have them.
     *
     * @param container The container to load data from
     */
    void load(PersistentDataContainer container);
}
