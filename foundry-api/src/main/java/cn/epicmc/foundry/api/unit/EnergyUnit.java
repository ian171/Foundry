package cn.epicmc.foundry.api.unit;

import org.bukkit.NamespacedKey;

/**
 * Represents an energy unit type in the Foundry framework.
 * <p>
 * Energy units measure electrical or mechanical energy stored and transferred
 * between machines. Different plugins may define their own energy units.
 * <p>
 * <b>Important:</b> Units with the same symbol (e.g., "EU") from different
 * plugins are NOT automatically compatible. Each energy type is uniquely
 * identified by its {@link NamespacedKey}.
 * <p>
 * Standard Foundry energy unit:
 * <ul>
 *   <li>Symbol: "EU" (Electric Unit)</li>
 *   <li>ID: {@code foundry:electric_unit}</li>
 * </ul>
 * <p>
 * Energy conversion between different energy types must be handled explicitly
 * through {@link cn.epicmc.foundry.api.energy.EnergyConverter}.
 *
 * @see Unit
 * @see cn.epicmc.foundry.api.energy.EnergyHandler
 * @see cn.epicmc.foundry.api.energy.EnergyConverter
 */
public interface EnergyUnit extends Unit {

    @Override
    default UnitCategory getCategory() {
        return UnitCategory.ENERGY;
    }

    /**
     * Check if this energy unit is compatible with another energy unit.
     * <p>
     * Two energy units are compatible if they have the same {@link NamespacedKey}.
     * Even if two units have the same symbol (e.g., both are called "EU"),
     * they are incompatible if registered by different plugins.
     *
     * @param other The other energy unit to check
     * @return {@code true} if the units are the same, {@code false} otherwise
     */
    default boolean isCompatibleWith(EnergyUnit other) {
        return this.getId().equals(other.getId());
    }

    /**
     * Get the base conversion rate to the standard unit.
     * <p>
     * This is used for display purposes and optional automatic conversion.
     * Return 1.0 if this is the standard unit or if conversion is not applicable.
     * <p>
     * Example: If 1 RF = 0.25 EU, then RF's base rate would be 0.25
     *
     * @return The conversion rate, or 1.0 if not applicable
     */
    default double getBaseConversionRate() {
        return 1.0;
    }
}
