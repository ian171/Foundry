package cn.epicmc.foundry.api.capability;

/**
 * Standard capability keys for the Foundry framework.
 * <p>
 * These constants provide type-safe access to all core capabilities.
 * Third-party plugins can define their own capability keys.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Query a capability
 * Optional<EnergyHandler> handler =
 *     machine.getCapability(FoundryCapabilities.ENERGY, Direction.UP);
 *
 * // Check if supported
 * if (machine.hasCapability(FoundryCapabilities.FLUID)) {
 *     // Machine can handle fluids
 * }
 * }</pre>
 */
public final class FoundryCapabilities {

    private FoundryCapabilities() {
        throw new UnsupportedOperationException("Utility class");
    }

    // TODO: Define capability keys when their handler interfaces are implemented
    // These are placeholders for the actual capability system

    /**
     * Energy capability - for energy storage and transfer.
     * <p>
     * Machines with this capability can:
     * <ul>
     *   <li>Store energy</li>
     *   <li>Receive energy from generators/networks</li>
     *   <li>Provide energy to consumers/networks</li>
     * </ul>
     * <p>
     * Example: Batteries, generators, electric machines
     */
    // public static final CapabilityKey<EnergyHandler> ENERGY =
    //     CapabilityKey.of("foundry", "energy", EnergyHandler.class);

    /**
     * Fluid capability - for fluid storage and transfer.
     * <p>
     * Machines with this capability can:
     * <ul>
     *   <li>Store fluids in tanks</li>
     *   <li>Receive fluids from pipes/networks</li>
     *   <li>Provide fluids to consumers/networks</li>
     * </ul>
     * <p>
     * Example: Tanks, pumps, fluid processing machines
     */
    // public static final CapabilityKey<FluidHandler> FLUID =
    //     CapabilityKey.of("foundry", "fluid", FluidHandler.class);

    /**
     * Item capability - for item storage and transfer.
     * <p>
     * Machines with this capability can:
     * <ul>
     *   <li>Store items in inventory slots</li>
     *   <li>Receive items from hoppers/networks</li>
     *   <li>Provide items to consumers/networks</li>
     * </ul>
     * <p>
     * Example: Chests, machines with input/output slots
     */
    // public static final CapabilityKey<ItemHandler> ITEM =
    //     CapabilityKey.of("foundry", "item", ItemHandler.class);

    /**
     * Heat capability - for temperature and heat transfer.
     * <p>
     * Machines with this capability have:
     * <ul>
     *   <li>Temperature (in Kelvin)</li>
     *   <li>Heat capacity</li>
     *   <li>Heat transfer rate</li>
     * </ul>
     * <p>
     * Example: Furnaces, boilers, reactors
     */
    // public static final CapabilityKey<HeatHandler> HEAT =
    //     CapabilityKey.of("foundry", "heat", HeatHandler.class);

    /**
     * Pressure capability - for pressure and gas handling.
     * <p>
     * Machines with this capability have:
     * <ul>
     *   <li>Internal pressure (in Pascals)</li>
     *   <li>Maximum safe pressure</li>
     *   <li>Pressure regulation</li>
     * </ul>
     * <p>
     * Example: Compressors, pressure vessels, pneumatic systems
     */
    // public static final CapabilityKey<PressureHandler> PRESSURE =
    //     CapabilityKey.of("foundry", "pressure", PressureHandler.class);

    /**
     * Recipe processor capability - for recipe-based machines.
     * <p>
     * Machines with this capability can:
     * <ul>
     *   <li>Process recipes</li>
     *   <li>Track processing progress</li>
     *   <li>Consume inputs and produce outputs</li>
     * </ul>
     * <p>
     * Example: Furnaces, crushers, chemical reactors
     */
    // public static final CapabilityKey<RecipeProcessor> RECIPE_PROCESSOR =
    //     CapabilityKey.of("foundry", "recipe_processor", RecipeProcessor.class);

    /**
     * Menu provider capability - for GUI access.
     * <p>
     * Machines with this capability can:
     * <ul>
     *   <li>Open GUIs for players</li>
     *   <li>Provide custom menus</li>
     *   <li>Handle player interactions</li>
     * </ul>
     * <p>
     * Example: Any machine with a GUI
     */
    // public static final CapabilityKey<MenuProvider> MENU =
    //     CapabilityKey.of("foundry", "menu", MenuProvider.class);

    /**
     * Machine controller capability - for machine control.
     * <p>
     * Machines with this capability can be:
     * <ul>
     *   <li>Started/stopped</li>
     *   <li>Paused/resumed</li>
     *   <li>Configured</li>
     * </ul>
     * <p>
     * Example: Most machines
     */
    // public static final CapabilityKey<MachineController> MACHINE =
    //     CapabilityKey.of("foundry", "machine", MachineController.class);
}
