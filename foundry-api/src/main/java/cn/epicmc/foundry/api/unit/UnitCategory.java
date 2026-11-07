package cn.epicmc.foundry.api.unit;

/**
 * Categories of units used in the Foundry framework.
 * <p>
 * Unit categories define what type of quantity is being measured.
 * Units within the same category can potentially be converted to each other,
 * while units of different categories are incompatible.
 *
 * @see Unit
 */
public enum UnitCategory {

    /**
     * Energy units - measures electrical or mechanical energy.
     * <p>
     * Examples: EU (Electric Unit), RF (Redstone Flux), FE (Forge Energy), J (Joules)
     */
    ENERGY("Energy"),

    /**
     * Fluid units - measures volume of liquids and gases.
     * <p>
     * Standard: mB (millibuckets), where 1000 mB = 1 bucket
     * <p>
     * Examples: mB (millibucket), L (liter), B (bucket)
     */
    FLUID("Fluid"),

    /**
     * Temperature units - measures thermal energy level.
     * <p>
     * Standard: K (Kelvin)
     * <p>
     * Examples: K (Kelvin), °C (Celsius), °F (Fahrenheit)
     */
    TEMPERATURE("Temperature"),

    /**
     * Pressure units - measures force per unit area.
     * <p>
     * Standard: Pa (Pascal)
     * <p>
     * Examples: Pa (Pascal), bar, atm (atmosphere), psi
     */
    PRESSURE("Pressure"),

    /**
     * Time units - measures duration.
     * <p>
     * Standard: tick (Minecraft game tick, 20 ticks = 1 second)
     * <p>
     * Examples: tick, second, minute
     */
    TIME("Time"),

    /**
     * Mass units - measures amount of matter.
     * <p>
     * Standard: kg (kilogram)
     * <p>
     * Examples: kg (kilogram), g (gram), t (ton)
     */
    MASS("Mass"),

    /**
     * Custom unit category for plugin-specific units.
     * <p>
     * Use this for units that don't fit into standard categories.
     */
    CUSTOM("Custom");

    private final String displayName;

    UnitCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the human-readable name of this category.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
