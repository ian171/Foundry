package cn.epicmc.foundry.api.network;

import java.util.UUID;

/**
 * Represents a request to transfer content between network nodes.
 * <p>
 * This is an immutable data object that describes what should be transferred.
 * The actual transfer logic is implemented by network-specific handlers.
 *
 * @param <T> The type of content being transferred (e.g., EnergyType, FluidStack, ItemStack)
 *
 * @since 1.0.0
 */
public interface TransferRequest<T> {

    /**
     * Get the ID of the source node.
     *
     * @return Source node UUID
     */
    UUID getSourceId();

    /**
     * Get the ID of the destination node.
     *
     * @return Destination node UUID
     */
    UUID getDestinationId();

    /**
     * Get the content to transfer.
     *
     * @return The content object
     */
    T getContent();

    /**
     * Get the amount to transfer.
     *
     * @return The transfer amount
     */
    double getAmount();

    /**
     * Check if this is a simulation (no actual transfer).
     *
     * @return true if simulating
     */
    boolean isSimulation();

    /**
     * Get the priority of this transfer.
     * <p>
     * Higher priority transfers are processed first.
     *
     * @return Priority value
     */
    default int getPriority() {
        return 0;
    }
}
