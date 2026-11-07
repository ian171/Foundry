package cn.epicmc.foundry.api.unit;

/**
 * Represents a pressure unit in the Foundry framework.
 * <p>
 * Pressure units measure force per unit area in fluid systems,
 * gas containers, and pressurized machinery.
 * <p>
 * <b>Standard Unit:</b> Pascal (Pa)
 * <ul>
 *   <li>Pascal is the SI unit of pressure</li>
 *   <li>1 Pa = 1 N/m² (Newton per square meter)</li>
 *   <li>All internal calculations use Pascals</li>
 * </ul>
 * <p>
 * Common conversions:
 * <ul>
 *   <li>1 bar = 100,000 Pa</li>
 *   <li>1 atm (atmosphere) = 101,325 Pa</li>
 *   <li>1 psi (pound per square inch) ≈ 6,894.76 Pa</li>
 * </ul>
 *
 * @see Unit
 */
public interface PressureUnit extends Unit {

    /**
     * Standard atmospheric pressure in Pascals (1 atm)
     */
    double STANDARD_ATMOSPHERE_PASCALS = 101325.0;

    /**
     * Bar to Pascal conversion (1 bar)
     */
    double BAR_TO_PASCALS = 100000.0;

    @Override
    default UnitCategory getCategory() {
        return UnitCategory.PRESSURE;
    }

    /**
     * Convert a pressure in this unit to Pascals (Pa).
     * <p>
     * This is the standard internal unit used throughout the API.
     *
     * @param pressure The pressure in this unit
     * @return The equivalent pressure in Pascals
     */
    double toPascals(double pressure);

    /**
     * Convert a pressure in Pascals (Pa) to this unit.
     * <p>
     * This is primarily used for display purposes.
     *
     * @param pascals The pressure in Pascals
     * @return The equivalent pressure in this unit
     */
    double fromPascals(double pascals);

    /**
     * Check if a pressure value is valid (non-negative).
     * <p>
     * Negative pressure values (vacuum below absolute zero pressure)
     * are not supported in this framework.
     *
     * @param pressure The pressure in this unit
     * @return {@code true} if valid, {@code false} if negative
     */
    default boolean isValid(double pressure) {
        return toPascals(pressure) >= 0.0;
    }
}
