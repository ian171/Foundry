package cn.epicmc.foundry.api.unit;

/**
 * Represents a temperature unit in the Foundry framework.
 * <p>
 * Temperature units measure thermal energy levels of fluids, machines,
 * and environmental conditions.
 * <p>
 * <b>Standard Unit:</b> Kelvin (K)
 * <ul>
 *   <li>Kelvin is the absolute temperature scale</li>
 *   <li>0 K = absolute zero (-273.15°C)</li>
 *   <li>All internal calculations use Kelvin</li>
 * </ul>
 * <p>
 * Other units (Celsius, Fahrenheit) are provided for display purposes.
 *
 * @see Unit
 */
public interface TemperatureUnit extends Unit {

    /**
     * Absolute zero in Celsius
     */
    double ABSOLUTE_ZERO_CELSIUS = -273.15;

    /**
     * Standard temperature in Kelvin (0°C)
     */
    double STANDARD_TEMPERATURE_KELVIN = 273.15;

    @Override
    default UnitCategory getCategory() {
        return UnitCategory.TEMPERATURE;
    }

    /**
     * Convert a temperature in this unit to Kelvin (K).
     * <p>
     * This is the standard internal unit used throughout the API.
     *
     * @param temperature The temperature in this unit
     * @return The equivalent temperature in Kelvin
     */
    double toKelvin(double temperature);

    /**
     * Convert a temperature in Kelvin (K) to this unit.
     * <p>
     * This is primarily used for display purposes.
     *
     * @param kelvin The temperature in Kelvin
     * @return The equivalent temperature in this unit
     */
    double fromKelvin(double kelvin);

    /**
     * Check if a temperature is valid (not below absolute zero).
     * <p>
     * Temperatures below absolute zero (0 K) are physically impossible.
     *
     * @param temperature The temperature in this unit
     * @return {@code true} if valid, {@code false} if below absolute zero
     */
    default boolean isValid(double temperature) {
        return toKelvin(temperature) >= 0.0;
    }
}
