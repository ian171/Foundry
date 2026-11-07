package cn.epicmc.foundry.api.registry;

import cn.epicmc.foundry.api.unit.EnergyUnit;
import cn.epicmc.foundry.api.unit.FluidUnit;
import cn.epicmc.foundry.api.unit.PressureUnit;
import cn.epicmc.foundry.api.unit.TemperatureUnit;

/**
 * Standard registry keys for the Foundry framework.
 * <p>
 * These constants provide type-safe access to all core registries.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Access a registry
 * Registry<EnergyUnit> energyRegistry =
 *     FoundryAPI.getRegistryManager()
 *               .getRegistry(FoundryRegistries.ENERGY_UNITS);
 *
 * // Register during REGISTRY phase
 * MutableRegistry<EnergyUnit> mutableRegistry =
 *     FoundryAPI.getRegistryManager()
 *               .getMutableRegistry(FoundryRegistries.ENERGY_UNITS);
 *
 * mutableRegistry.register(myUnit.getId(), myUnit);
 * }</pre>
 */
public final class FoundryRegistries {

    private FoundryRegistries() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Registry for energy unit types.
     * <p>
     * Energy units define how energy is measured and displayed.
     * Different plugins can register their own energy types.
     * <p>
     * <b>Registration Phase:</b> REGISTRY<br>
     * <b>Dynamic Registration:</b> Not supported
     */
    public static final RegistryKey<EnergyUnit> ENERGY_UNITS =
        RegistryKey.of("foundry", "energy_unit", EnergyUnit.class);

    /**
     * Registry for fluid unit types.
     * <p>
     * Fluid units define how fluid volume is measured and displayed.
     * <p>
     * <b>Registration Phase:</b> REGISTRY<br>
     * <b>Dynamic Registration:</b> Not supported
     */
    public static final RegistryKey<FluidUnit> FLUID_UNITS =
        RegistryKey.of("foundry", "fluid_unit", FluidUnit.class);

    /**
     * Registry for temperature unit types.
     * <p>
     * Temperature units define how temperature is measured and displayed.
     * <p>
     * <b>Registration Phase:</b> REGISTRY<br>
     * <b>Dynamic Registration:</b> Not supported
     */
    public static final RegistryKey<TemperatureUnit> TEMPERATURE_UNITS =
        RegistryKey.of("foundry", "temperature_unit", TemperatureUnit.class);

    /**
     * Registry for pressure unit types.
     * <p>
     * Pressure units define how pressure is measured and displayed.
     * <p>
     * <b>Registration Phase:</b> REGISTRY<br>
     * <b>Dynamic Registration:</b> Not supported
     */
    public static final RegistryKey<PressureUnit> PRESSURE_UNITS =
        RegistryKey.of("foundry", "pressure_unit", PressureUnit.class);

    // TODO: Add more registry keys as needed:
    // - MACHINE_DEFINITIONS

    // - BLOCK_DEFINITIONS
    // - FLUID_TYPES
    // - RECIPE_TYPES
    // - CAPABILITY_TYPES
    // - NETWORK_TYPES
}
