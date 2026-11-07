package cn.epicmc.foundry.api.unit;

/**
 * Represents a fluid volume unit in the Foundry framework.
 * <p>
 * Fluid units measure the volume of liquids and gases stored in tanks,
 * pipes, and other fluid containers.
 * <p>
 * <b>Standard Unit:</b> Millibucket (mB)
 * <ul>
 *   <li>1000 mB = 1 Bucket (standard Minecraft bucket)</li>
 *   <li>1 mB is the smallest measurable fluid amount</li>
 * </ul>
 * <p>
 * All fluid amounts in the API use {@code long} values in millibuckets (mB)
 * to ensure precision and avoid floating-point errors.
 *
 * @see Unit
 * @see cn.epicmc.foundry.api.fluid.FluidStack
 * @see cn.epicmc.foundry.api.fluid.FluidTank
 */
public interface FluidUnit extends Unit {

    /**
     * Standard conversion: 1000 millibuckets = 1 bucket
     */
    long MILLIBUCKETS_PER_BUCKET = 1000L;

    @Override
    default UnitCategory getCategory() {
        return UnitCategory.FLUID;
    }

    /**
     * Convert an amount in this unit to millibuckets (mB).
     * <p>
     * This is the standard internal unit used throughout the API.
     * All fluid calculations should be done in millibuckets.
     *
     * @param amount The amount in this unit
     * @return The equivalent amount in millibuckets
     */
    long toMillibuckets(double amount);

    /**
     * Convert an amount in millibuckets (mB) to this unit.
     * <p>
     * This is primarily used for display purposes.
     *
     * @param millibuckets The amount in millibuckets
     * @return The equivalent amount in this unit
     */
    double fromMillibuckets(long millibuckets);

    /**
     * Get the number of millibuckets per unit.
     * <p>
     * Examples:
     * <ul>
     *   <li>Millibucket (mB): 1</li>
     *   <li>Bucket (B): 1000</li>
     *   <li>Liter (L): 1000</li>
     * </ul>
     *
     * @return Millibuckets per unit
     */
    long getMillibucketsPerUnit();
}
