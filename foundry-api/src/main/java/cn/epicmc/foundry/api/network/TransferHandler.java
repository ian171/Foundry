package cn.epicmc.foundry.api.network;

import java.util.concurrent.CompletableFuture;

/**
 * Handles content transfers between network nodes.
 * <p>
 * Each network type (energy, fluid, items) implements its own transfer handler
 * that defines how content moves between nodes.
 * <p>
 * <b>Thread Safety:</b> All methods must be thread-safe as they may be called
 * from virtual threads concurrently.
 *
 * @param <T> The type of content this handler transfers
 *
 * @since 1.0.0
 */
public interface TransferHandler<T> {

    /**
     * Get the network type this handler supports.
     *
     * @return The network type
     */
    NetworkType getNetworkType();

    /**
     * Execute a transfer request.
     * <p>
     * This runs asynchronously on a virtual thread and returns a future.
     * Implementations should:
     * <ol>
     *   <li>Validate the request</li>
     *   <li>Check source can send and destination can receive</li>
     *   <li>Calculate actual transfer amount</li>
     *   <li>Perform the transfer (if not simulating)</li>
     *   <li>Return the result</li>
     * </ol>
     *
     * @param request The transfer request
     * @return A future that completes with the transfer result
     */
    CompletableFuture<TransferResult> transfer(TransferRequest<T> request);

    /**
     * Calculate the maximum amount that can be transferred.
     * <p>
     * This is used for transfer planning and optimization.
     *
     * @param sourceId Source node ID
     * @param destinationId Destination node ID
     * @return Maximum transferable amount
     */
    double calculateMaxTransfer(java.util.UUID sourceId, java.util.UUID destinationId);

    /**
     * Check if a transfer between two nodes is possible.
     *
     * @param sourceId Source node ID
     * @param destinationId Destination node ID
     * @return true if transfer is possible
     */
    boolean canTransfer(java.util.UUID sourceId, java.util.UUID destinationId);
}
