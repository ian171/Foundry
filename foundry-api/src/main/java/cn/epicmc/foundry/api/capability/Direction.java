package cn.epicmc.foundry.api.capability;

import org.bukkit.block.BlockFace;

/**
 * Represents a direction for capability queries.
 * <p>
 * Directions indicate which side of a block/machine is being accessed.
 * This allows machines to have different capabilities on different sides.
 * <p>
 * For example, a furnace might:
 * <ul>
 *   <li>Accept items from the TOP</li>
 *   <li>Output items to the BOTTOM</li>
 *   <li>Accept fuel from any side</li>
 * </ul>
 * <p>
 * The direction is from the perspective of the <b>accessor</b>, not the provider.
 * If a pipe is NORTH of a machine, it queries the machine's SOUTH side.
 */
public enum Direction {

    /**
     * North direction (-Z axis)
     */
    NORTH,

    /**
     * South direction (+Z axis)
     */
    SOUTH,

    /**
     * East direction (+X axis)
     */
    EAST,

    /**
     * West direction (-X axis)
     */
    WEST,

    /**
     * Up direction (+Y axis)
     */
    UP,

    /**
     * Down direction (-Y axis)
     */
    DOWN,

    /**
     * No specific direction / internal access.
     * <p>
     * Used when:
     * <ul>
     *   <li>Accessing from GUI</li>
     *   <li>Internal operations</li>
     *   <li>Direction doesn't matter</li>
     * </ul>
     */
    UNKNOWN;

    /**
     * Get the opposite direction.
     * <p>
     * Examples:
     * <ul>
     *   <li>NORTH → SOUTH</li>
     *   <li>UP → DOWN</li>
     *   <li>UNKNOWN → UNKNOWN</li>
     * </ul>
     *
     * @return The opposite direction
     */
    public Direction getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
            case UP -> DOWN;
            case DOWN -> UP;
            case UNKNOWN -> UNKNOWN;
        };
    }

    /**
     * Convert a Bukkit BlockFace to a Direction.
     *
     * @param face The block face
     * @return The corresponding direction
     */
    public static Direction fromBlockFace(BlockFace face) {
        return switch (face) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
            default -> UNKNOWN;
        };
    }

    /**
     * Convert this direction to a Bukkit BlockFace.
     *
     * @return The corresponding block face
     * @throws IllegalStateException if this is UNKNOWN
     */
    public BlockFace toBlockFace() {
        return switch (this) {
            case NORTH -> BlockFace.NORTH;
            case SOUTH -> BlockFace.SOUTH;
            case EAST -> BlockFace.EAST;
            case WEST -> BlockFace.WEST;
            case UP -> BlockFace.UP;
            case DOWN -> BlockFace.DOWN;
            case UNKNOWN -> throw new IllegalStateException("Cannot convert UNKNOWN to BlockFace");
        };
    }

    /**
     * Check if this is a horizontal direction (NORTH, SOUTH, EAST, WEST).
     *
     * @return {@code true} if horizontal, {@code false} otherwise
     */
    public boolean isHorizontal() {
        return this == NORTH || this == SOUTH || this == EAST || this == WEST;
    }

    /**
     * Check if this is a vertical direction (UP, DOWN).
     *
     * @return {@code true} if vertical, {@code false} otherwise
     */
    public boolean isVertical() {
        return this == UP || this == DOWN;
    }
}
