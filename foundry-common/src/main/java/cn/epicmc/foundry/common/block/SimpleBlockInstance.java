package cn.epicmc.foundry.common.block;

import cn.epicmc.foundry.api.block.BlockInstance;
import cn.epicmc.foundry.api.block.BlockState;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.util.Objects;
import java.util.UUID;

/**
 * Simple implementation of BlockInstance.
 */
public class SimpleBlockInstance implements BlockInstance {

    private final UUID uuid;
    private final NamespacedKey definitionId;
    private final Location location;
    private BlockState state;

    public SimpleBlockInstance(
        NamespacedKey definitionId,
        Location location
    ) {
        this(UUID.randomUUID(), definitionId, location);
    }

    public SimpleBlockInstance(
        UUID uuid,
        NamespacedKey definitionId,
        Location location
    ) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
        this.definitionId = Objects.requireNonNull(definitionId, "definitionId");
        this.location = Objects.requireNonNull(location, "location").clone();
        this.state = BlockState.IDLE;
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
