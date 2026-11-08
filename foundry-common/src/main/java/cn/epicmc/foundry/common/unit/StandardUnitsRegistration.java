package cn.epicmc.foundry.common.unit;

import cn.epicmc.foundry.api.registry.FoundryRegistries;
import cn.epicmc.foundry.api.registry.MutableRegistry;
import cn.epicmc.foundry.api.registry.RegistryManager;

/**
 * Utility class for registering standard Foundry units.
 */
public final class StandardUnitsRegistration {

    private StandardUnitsRegistration() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Register all standard units in the provided registry manager.
     *
     * @param registryManager The registry manager
     */
    public static void registerAll(RegistryManager registryManager) {
        registerEnergyUnits(registryManager);
        registerFluidUnits(registryManager);
        registerTemperatureUnits(registryManager);
        registerPressureUnits(registryManager);
    }

    /**
     * Register standard energy units.
     */
    private static void registerEnergyUnits(RegistryManager registryManager) {
        MutableRegistry<cn.epicmc.foundry.api.unit.EnergyUnit> registry =
            registryManager.getMutableRegistry(FoundryRegistries.ENERGY_UNITS);

        registry.register(StandardUnits.EU.getId(), StandardUnits.EU);
    }

    /**
     * Register standard fluid units.
     */
    private static void registerFluidUnits(RegistryManager registryManager) {
        MutableRegistry<cn.epicmc.foundry.api.unit.FluidUnit> registry =
            registryManager.getMutableRegistry(FoundryRegistries.FLUID_UNITS);

        registry.register(StandardUnits.MILLIBUCKET.getId(), StandardUnits.MILLIBUCKET);
        registry.register(StandardUnits.BUCKET.getId(), StandardUnits.BUCKET);
    }

    /**
     * Register standard temperature units.
     */
    private static void registerTemperatureUnits(RegistryManager registryManager) {
        MutableRegistry<cn.epicmc.foundry.api.unit.TemperatureUnit> registry =
            registryManager.getMutableRegistry(FoundryRegistries.TEMPERATURE_UNITS);

        registry.register(StandardUnits.KELVIN.getId(), StandardUnits.KELVIN);
        registry.register(StandardUnits.CELSIUS.getId(), StandardUnits.CELSIUS);
    }

    /**
     * Register standard pressure units.
     */
    private static void registerPressureUnits(RegistryManager registryManager) {
        MutableRegistry<cn.epicmc.foundry.api.unit.PressureUnit> registry =
            registryManager.getMutableRegistry(FoundryRegistries.PRESSURE_UNITS);

        registry.register(StandardUnits.PASCAL.getId(), StandardUnits.PASCAL);
        registry.register(StandardUnits.BAR.getId(), StandardUnits.BAR);
        registry.register(StandardUnits.ATMOSPHERE.getId(), StandardUnits.ATMOSPHERE);
    }
}
