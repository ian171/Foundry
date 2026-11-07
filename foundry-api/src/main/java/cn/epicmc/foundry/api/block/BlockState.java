package cn.epicmc.foundry.api.block;

/**
 * Represents the operational state of a block.
 * <p>
 * Block states are used to track the runtime status of machines and
 * other interactive blocks.
 */
public enum BlockState {

    /**
     * The block is idle and not performing any operations.
     * <p>
     * This is the default state for most blocks when they are not active.
     */
    IDLE,

    /**
     * The block is actively performing operations.
     * <p>
     * For machines: processing recipes, consuming energy, producing outputs.
     * Visual indicators (particles, sounds) should reflect this state.
     */
    ACTIVE,

    /**
     * The block is temporarily paused.
     * <p>
     * Operations are suspended but can be resumed. State is preserved.
     * Examples:
     * <ul>
     *   <li>Waiting for input resources</li>
     *   <li>Output inventory is full</li>
     *   <li>Manually paused by player</li>
     * </ul>
     */
    PAUSED,

    /**
     * The block is disabled and will not operate.
     * <p>
     * This is a long-term stopped state, typically set by:
     * <ul>
     *   <li>Player configuration</li>
     *   <li>Redstone signal</li>
     *   <li>Administrative command</li>
     * </ul>
     */
    DISABLED,

    /**
     * The block is in an error state.
     * <p>
     * Something went wrong during operation. The block cannot continue
     * until the error is resolved. Common causes:
     * <ul>
     *   <li>Invalid recipe configuration</li>
     *   <li>Corrupted data</li>
     *   <li>Missing dependencies</li>
     *   <li>Overheated/overpressure conditions</li>
     * </ul>
     */
    ERROR
}
