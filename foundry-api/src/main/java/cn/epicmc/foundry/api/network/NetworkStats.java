package cn.epicmc.foundry.api.network;

/**
 * Statistics about a network's performance and state.
 * <p>
 * This is an immutable snapshot of the network's metrics at a point in time.
 *
 * @param nodeCount Number of nodes in the network
 * @param connectionCount Total number of connections between nodes
 * @param transfersPerTick Average transfers processed per tick
 * @param lastUpdateTime Timestamp of the last topology update (millis)
 * @param isHealthy Whether the network is operating normally
 *
 * @since 1.0.0
 */
public record NetworkStats(
    int nodeCount,
    int connectionCount,
    double transfersPerTick,
    long lastUpdateTime,
    boolean isHealthy
) {

    /**
     * Create stats for an empty/invalid network.
     */
    public static NetworkStats empty() {
        return new NetworkStats(0, 0, 0.0, 0, false);
    }
}
