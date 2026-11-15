package cn.epicmc.foundry.network;

import cn.epicmc.foundry.api.network.NetworkNode;
import cn.epicmc.foundry.api.network.NetworkType;
import org.bukkit.Location;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple thread-safe implementation of a network node.
 *
 * @since 1.0.0
 */
public class SimpleNetworkNode implements NetworkNode {

    private final UUID nodeId;
    private final NetworkType networkType;
    private final Location location;
    private final Set<UUID> connections;
    private volatile boolean canReceive;
    private volatile boolean canSend;
    private volatile int priority;

    public SimpleNetworkNode(
        UUID nodeId,
        NetworkType networkType,
        Location location
    ) {
        this.nodeId = Objects.requireNonNull(nodeId, "nodeId");
        this.networkType = Objects.requireNonNull(networkType, "networkType");
        this.location = Objects.requireNonNull(location, "location").clone();
        this.connections = ConcurrentHashMap.newKeySet();
        this.canReceive = true;
        this.canSend = true;
        this.priority = 0;
    }

    @Override
    public UUID getNodeId() {
        return nodeId;
    }

    @Override
    public NetworkType getNetworkType() {
        return networkType;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public Set<UUID> getConnections() {
        return Set.copyOf(connections);
    }

    @Override
    public boolean isValid() {
        return location.getWorld() != null &&
               location.getWorld().isChunkLoaded(
                   location.getBlockX() >> 4,
                   location.getBlockZ() >> 4
               );
    }

    @Override
    public boolean canReceive() {
        return canReceive;
    }

    @Override
    public boolean canSend() {
        return canSend;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * Set whether this node can receive content.
     *
     * @param canReceive true if can receive
     */
    public void setCanReceive(boolean canReceive) {
        this.canReceive = canReceive;
    }

    /**
     * Set whether this node can send content.
     *
     * @param canSend true if can send
     */
    public void setCanSend(boolean canSend) {
        this.canSend = canSend;
    }

    /**
     * Set the priority of this node.
     *
     * @param priority The priority value
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Add a connection to another node.
     * <p>
     * <b>Thread Safety:</b> This method is thread-safe.
     *
     * @param nodeId The ID of the connected node
     */
    public void addConnection(UUID nodeId) {
        connections.add(nodeId);
    }

    /**
     * Remove a connection to another node.
     * <p>
     * <b>Thread Safety:</b> This method is thread-safe.
     *
     * @param nodeId The ID of the node to disconnect
     * @return true if the connection was removed
     */
    public boolean removeConnection(UUID nodeId) {
        return connections.remove(nodeId);
    }

    /**
     * Clear all connections.
     */
    public void clearConnections() {
        connections.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetworkNode that)) return false;
        return nodeId.equals(that.getNodeId());
    }

    @Override
    public int hashCode() {
        return nodeId.hashCode();
    }

    @Override
    public String toString() {
        return "NetworkNode{" +
               "id=" + nodeId +
               ", type=" + networkType.getId() +
               ", location=" + location.getBlockX() + "," +
                              location.getBlockY() + "," +
                              location.getBlockZ() +
               ", connections=" + connections.size() +
               '}';
    }
}
