package cn.epicmc.foundry.api.unit;

import org.bukkit.NamespacedKey;

/**
 * Base interface for all unit types in the Foundry framework.
 * <p>
 * Units represent measurable quantities like energy, fluids, temperature, pressure, etc.
 * All units must have a globally unique identifier and a display name.
 * <p>
 * Thread Safety: Implementations should be immutable and thread-safe.
 *
 * @see EnergyUnit
 * @see FluidUnit
 * @see TemperatureUnit
 * @see PressureUnit
 */
public interface Unit {

    /**
     * Get the unique identifier for this unit type.
     * <p>
     * The namespace typically identifies the plugin that registered this unit.
     * The key identifies the specific unit within that plugin.
     * <p>
     * Example: {@code foundry:electric_unit}, {@code example:rf}
     *
     * @return The unique namespaced identifier
     */
    NamespacedKey getId();

    /**
     * Get the symbol used to display this unit.
     * <p>
     * This is a short, human-readable representation of the unit.
     * <p>
     * Examples:
     * <ul>
     *   <li>Energy: "EU", "RF", "FE", "J"</li>
     *   <li>Fluid: "mB", "L", "B"</li>
     *   <li>Temperature: "K", "°C", "°F"</li>
     *   <li>Pressure: "Pa", "bar", "atm"</li>
     * </ul>
     *
     * @return The unit symbol for display purposes
     */
    String getSymbol();

    /**
     * Get the full name of this unit.
     * <p>
     * This is a descriptive name that can be used in tooltips or documentation.
     * <p>
     * Examples: "Electric Unit", "Redstone Flux", "Millibucket", "Kelvin"
     *
     * @return The full name of the unit
     */
    String getName();

    /**
     * Get the category of this unit.
     * <p>
     * Units of the same category can potentially be converted between each other.
     * Units of different categories cannot be converted.
     *
     * @return The unit category
     */
    UnitCategory getCategory();

    /**
     * Check if this unit can be converted to another unit.
     * <p>
     * Conversion is only possible between units of the same category.
     * Some units within the same category may not have a defined conversion rate.
     *
     * @param target The target unit to convert to
     * @return {@code true} if conversion is possible, {@code false} otherwise
     */
    default boolean canConvertTo(Unit target) {
        return this.getCategory() == target.getCategory();
    }
}
