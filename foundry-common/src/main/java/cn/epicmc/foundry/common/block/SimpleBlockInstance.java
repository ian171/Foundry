package cn.epicmc.foundry.common.block;

import cn.epicmc.foundry.api.block.BlockInstance;
import cn.epicmc.foundry.api.block.BlockState;
import cn.epicmc.foundry.common.nbt.NBTSerializable;
import cn.epicmc.foundry.common.nbt.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Objects;
import java.util.UUID;

/**
 * Simple implementation of BlockInstance with NBT persistence support.
 * <p>
 * This implementation can be saved to and loaded from PersistentDataContainer,
 * making it suitable for:
 * <ul>
 *   <li>Block entity storage (via TileState)</li>
 *   <li>Item NBT (for portable machines)</li>
 *   <li>Database serialization</li>
 * </ul>
 * <p>
 * <b>Example Usage:</b>
 * <pre>{@code
 * // Save to block
 * BlockState blockState = block.getState();
 * if (blockState instanceof TileState tile) {
 *     PersistentDataContainer container = tile.getPersistentDataContainer();
 *     blockInstance.save(container);
 *     blockState.update();
 * }
 *
 * // Load from block
 * BlockState blockState = block.getState();
 * if (blockState instanceof TileState tile) {
 *     PersistentDataContainer container = tile.getPersistentDataContainer();
 *     SimpleBlockInstance instance = new SimpleBlockInstance();
 *     instance.load(container);
 * }
 * }</pre>
 */
public class SimpleBlockInstance implements BlockInstance, NBTSerializable {

    private UUID uuid;
    private NamespacedKey definitionId;
    private Location location;
    private BlockState state;

    /**
     * Create a new block instance.
     * <p>
     * Use this constructor when placing a new block.
     *
     * @param definitionId The block definition ID
     * @param location The block location
     */
    public SimpleBlockInstance(
        NamespacedKey definitionId,
        Location location
    ) {
        this(UUID.randomUUID(), definitionId, location, BlockState.IDLE);
    }

    /**
     * Create a block instance with specific UUID and state.
     * <p>
     * Use this constructor when loading from persistence.
     *
     * @param uuid The unique identifier
     * @param definitionId The block definition ID
     * @param location The block location
     * @param state The block state
     */
    public SimpleBlockInstance(
        UUID uuid,
        NamespacedKey definitionId,
        Location location,
        BlockState state
    ) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
        this.definitionId = Objects.requireNonNull(definitionId, "definitionId");
        this.location = Objects.requireNonNull(location, "location").clone();
        this.state = Objects.requireNonNull(state, "state");
    }

    /**
     * Default constructor for deserialization.
     * <p>
     * Must call {@link #load(PersistentDataContainer)} after construction.
     */
    public SimpleBlockInstance() {
        // Fields will be set by load()
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public NamespacedKey getDefinitionId() {
        return definitionId;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public BlockState getState() {
        return state;
    }

    /**
     * Set the state of this block.
     *
     * @param state The new state
     */
    public void setState(BlockState state) {
        this.state = Objects.requireNonNull(state, "state");
    }

    @Override
    public boolean isLoaded() {
        return location.getWorld() != null &&
               location.getWorld().isChunkLoaded(
                   location.getBlockX() >> 4,
                   location.getBlockZ() >> 4
               );
    }

    @Override
    public boolean isValid() {
        return location.getWorld() != null && isLoaded();
    }

    // ==================== NBT Serialization ====================

    @Override
    public void save(PersistentDataContainer container) {
        // Save version for future migration
        NBTUtil.setInt(container, "data_version", 1);

        // Save UUID
        NBTUtil.setUUID(container, "uuid", uuid);

        // Save definition ID
        NBTUtil.setString(container, "definition_namespace", definitionId.getNamespace());
        NBTUtil.setString(container, "definition_key", definitionId.getKey());

        // Save location
        saveLocation(container, location);

        // Save state
        NBTUtil.setEnum(container, "state", state);
    }

    @Override
    public void load(PersistentDataContainer container) {
        // Check version
        int version = NBTUtil.getInt(container, "data_version", 1);

        switch (version) {
            case 1 -> loadV1(container);
            default -> throw new IllegalStateException("Unknown data version: " + version);
        }
    }

    /**
     * Load version 1 data format.
     */
    private void loadV1(PersistentDataContainer container) {
        // Load UUID
        this.uuid = NBTUtil.getUUID(container, "uuid", UUID.randomUUID());

        // Load definition ID
        String namespace = NBTUtil.getString(container, "definition_namespace", "foundry");
        String key = NBTUtil.getString(container, "definition_key", "unknown");
        this.definitionId = new NamespacedKey(namespace, key);

        // Load location
        this.location = loadLocation(container);

        // Load state
        this.state = NBTUtil.getEnum(container, "state", BlockState.class, BlockState.IDLE);
    }

    /**
     * Save location to container.
     */
    private void saveLocation(PersistentDataContainer container, Location loc) {
        if (loc.getWorld() != null) {
            NBTUtil.setString(container, "world", loc.getWorld().getUID().toString());
        }
        NBTUtil.setDouble(container, "x", loc.getX());
        NBTUtil.setDouble(container, "y", loc.getY());
        NBTUtil.setDouble(container, "z", loc.getZ());
        NBTUtil.setFloat(container, "yaw", loc.getYaw());
        NBTUtil.setFloat(container, "pitch", loc.getPitch());
    }

    /**
     * Load location from container.
     */
    private Location loadLocation(PersistentDataContainer container) {
        String worldUUID = NBTUtil.getString(container, "world", null);
        World world = null;
        if (worldUUID != null) {
            try {
                world = Bukkit.getWorld(UUID.fromString(worldUUID));
            } catch (IllegalArgumentException e) {
                // Invalid UUID, world will be null
            }
        }

        double x = NBTUtil.getDouble(container, "x", 0.0);
        double y = NBTUtil.getDouble(container, "y", 0.0);
        double z = NBTUtil.getDouble(container, "z", 0.0);
        float yaw = NBTUtil.getFloat(container, "yaw", 0.0f);
        float pitch = NBTUtil.getFloat(container, "pitch", 0.0f);

        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "BlockInstance{" +
               "uuid=" + uuid +
               ", definition=" + definitionId +
               ", location=" + location.getBlockX() + "," +
                               location.getBlockY() + "," +
                               location.getBlockZ() +
               ", state=" + state +
               "}";
    }
}
