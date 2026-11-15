package cn.epicmc.foundry.api.network;

/**
 * Global statistics across all networks.
 *
 * @param totalNetworks Total number of active networks
 * @param totalNodes Total number of registered nodes
 * @param totalConnections Total connections across all networks
 * @param averageNetworkSize Average nodes per network
 * @param updatesPerSecond Network topology updates per second
 * @param virtualThreadCount Number of active virtual threads for network operations
 *
 * @since 1.0.0
 */
public record GlobalNetworkStats(
    int totalNetworks,
    int totalNodes,
    int totalConnections,
    double averageNetworkSize,
    double updatesPerSecond,
    int virtualThreadCount
) {

    public static GlobalNetworkStats empty() {
        return new GlobalNetworkStats(0, 0, 0, 0.0, 0.0, 0);
    }
}
