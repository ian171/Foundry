package cn.epicmc.foundry.api.network;

/**
 * Result of a transfer operation.
 * <p>
 * Provides information about what was actually transferred and whether it succeeded.
 *
 * @param success Whether the transfer completed successfully
 * @param amountTransferred The actual amount transferred (may be less than requested)
 * @param message Optional message explaining the result
 *
 * @since 1.0.0
 */
public record TransferResult(
    boolean success,
    double amountTransferred,
    String message
) {

    /**
     * Create a successful transfer result.
     */
    public static TransferResult success(double amount) {
        return new TransferResult(true, amount, null);
    }

    /**
     * Create a failed transfer result.
     */
    public static TransferResult failure(String message) {
        return new TransferResult(false, 0.0, message);
    }

    /**
     * Create a partial transfer result.
     */
    public static TransferResult partial(double amount, String message) {
        return new TransferResult(true, amount, message);
    }
}
