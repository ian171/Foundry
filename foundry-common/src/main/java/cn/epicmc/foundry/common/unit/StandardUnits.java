package cn.epicmc.foundry.common.unit;

import cn.epicmc.foundry.api.unit.*;
import org.bukkit.NamespacedKey;

/**
 * Standard unit implementations provided by the Foundry framework.
 * <p>
 * These are the default units used by Foundry's core systems.
 * Third-party plugins can register their own units or use these standard units.
 */
public final class StandardUnits {

    private StandardUnits() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== Energy Units ====================

    /**
     * Electric Unit (EU) - Foundry's standard energy unit.
     */
    public static final EnergyUnit EU = new EnergyUnit() {
        private static final NamespacedKey ID = new NamespacedKey("foundry", "electric_unit");

        @Override
        public NamespacedKey getId() {
            return ID;
        }

        @Override
        public String getSymbol() {
            return "EU";
        }

        @Override
        public String getName() {
            return "Electric Unit";
        }
    };

    // ==================== Fluid Units ====================

    /**
     * Millibucket (mB) - Foundry's standard fluid unit.
     * <p>
     * 1000 mB = 1 Bucket (standard Minecraft bucket)
     */
    public static final FluidUnit MILLIBUCKET = new FluidUnit() {
        private static final NamespacedKey ID = new NamespacedKey("foundry", "millibucket");

        @Override
        public NamespacedKey getId() {
            return ID;
        }

        @Override
        public String getSymbol() {
            return "mB";
        }

        @Override
        public String getName() {
            return "Millibucket";
        }

        @Override
        public long toMillibuckets(double amount) {
            return (long) amount;
        }

        @Override
        public double fromMillibuckets(long millibuckets) {
            return millibuckets;
        }

        @Override
        public long getMillibucketsPerUnit() {
            return 1L;
        }
    };

    /**
     * Bucket (B) - Standard Minecraft bucket.
     * <p>
     * 1 Bucket = 1000 mB
     */
    public static final FluidUnit BUCKET = new FluidUnit() {
        private static final NamespacedKey ID = new NamespacedKey("foundry", "bucket");

        @Override
        public NamespacedKey getId() {
            return ID;
        }

        @Override
        public String getSymbol() {
            return "B";
        }

        @Override
        public String getName() {
            return "Bucket";
        }

        @Override
        public long toMillibuckets(double amount) {
            return (long) (amount * MILLIBUCKETS_PER_BUCKET);
        }

        @Override
        public double fromMillibuckets(long millibuckets) {
            return millibuckets / (double) MILLIBUCKETS_PER_BUCKET;
        }

        @Override
        public long getMillibucketsPerUnit() {
            return MILLIBUCKETS_PER_BUCKET;
        }
    };

    // ==================== Temperature Units ====================

    /**
     * Kelvin (K) - Absolute temperature scale.
     * <p>
     * This is the standard internal unit for temperature.
     */
    public static final TemperatureUnit KELVIN = new TemperatureUnit() {
        private static final NamespacedKey ID = new NamespacedKey("foundry", "kelvin");

        @Override
        public NamespacedKey getId() {
            return ID;
        }

        @Override
        public String getSymbol() {
            return "K";
        }

        @Override
        public String getName() {
            return "Kelvin";
        }

        @Override
        public double toKelvin(double temperature) {
            return temperature;
        }

        @Override
        public double fromKelvin(double kelvin) {
            return kelvin;
        }
    };

    /**
     * Celsius (°C) - Common temperature scale.
     * <p>
     * Conversion: K = °C + 273.15
     */
    public static final TemperatureUnit CELSIUS = new TemperatureUnit() {
        private static final NamespacedKey ID = new NamespacedKey("foundry", "celsius");

        @Override
        public NamespacedKey getId() {
            return ID;
        }

        @Override
        public String getSymbol() {
            return "°C";
        }

        @Override
        public String getName() {
            return "Celsius";
        }

        @Override
        public double toKelvin(double temperature) {
            return temperature + STANDARD_TEMPERATURE_KELVIN;
        }

        @Override
        public double fromKelvin(double kelvin) {
            return kelvin - STANDARD_TEMPERATURE_KELVIN;
        }
    };

    // ==================== Pressure Units ====================

    /**
     * Pascal (Pa) - SI unit of pressure.
     * <p>
     * This is the standard internal unit for pressure.
     */
    public static final PressureUnit PASCAL = new PressureUnit() {
        private static final NamespacedKey ID = new NamespacedKey("foundry", "pascal");

        @Override
        public NamespacedKey getId() {
            return ID;
        }

        @Override
        public String getSymbol() {
            return "Pa";
        }

        @Override
        public String getName() {
            return "Pascal";
        }

        @Override
        public double toPascals(double pressure) {
            return pressure;
        }

        @Override
        public double fromPascals(double pascals) {
            return pascals;
        }
    };

    /**
     * Bar - Common pressure unit.
     * <p>
     * 1 bar = 100,000 Pa
     */
    public static final PressureUnit BAR = new PressureUnit() {
        private static final NamespacedKey ID = new NamespacedKey("foundry", "bar");

        @Override
        public NamespacedKey getId() {
            return ID;
        }

        @Override
        public String getSymbol() {
            return "bar";
        }

        @Override
        public String getName() {
            return "Bar";
        }

        @Override
        public double toPascals(double pressure) {
            return pressure * BAR_TO_PASCALS;
        }

        @Override
        public double fromPascals(double pascals) {
            return pascals / BAR_TO_PASCALS;
        }
    };

    /**
     * Atmosphere (atm) - Standard atmospheric pressure unit.
     * <p>
     * 1 atm = 101,325 Pa
     */
    public static final PressureUnit ATMOSPHERE = new PressureUnit() {
        private static final NamespacedKey ID = new NamespacedKey("foundry", "atmosphere");

        @Override
        public NamespacedKey getId() {
            return ID;
        }

        @Override
        public String getSymbol() {
            return "atm";
        }

        @Override
        public String getName() {
            return "Atmosphere";
        }

        @Override
        public double toPascals(double pressure) {
            return pressure * STANDARD_ATMOSPHERE_PASCALS;
        }

        @Override
        public double fromPascals(double pascals) {
            return pascals / STANDARD_ATMOSPHERE_PASCALS;
        }
    };
}
