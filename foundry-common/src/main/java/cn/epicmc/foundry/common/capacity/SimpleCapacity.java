package cn.epicmc.foundry.common.capacity;

import cn.epicmc.foundry.api.Capacity;
import cn.epicmc.foundry.api.unit.Unit;
import cn.epicmc.foundry.common.nbt.NBTSerializable;
import cn.epicmc.foundry.common.nbt.NBTUtil;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Objects;

/**
 * Simple implementation of Capacity with NBT persistence support.
 * <p>
 * This is a basic container that tracks:
 * <ul>
 *   <li>Current stored amount</li>
 *   <li>Maximum capacity</li>
 *   <li>Temperature range and current temperature</li>
 *   <li>Transfer rates (input/output per tick)</li>
 * </ul>
 * <p>
 * <b>Usage Example - Energy Storage:</b>
 * <pre>{@code
 * SimpleCapacity energyStorage = new SimpleCapacity(
 *     EnergyUnit.EU,
 *     10000.0,  // 10k EU capacity
 *     0.0f,     // Min temp (not used for energy)
 *     1000.0f,  // Max temp (not used for energy)
 *     true      // Can contain permanently
 * );
 *
 * // Insert energy
 * double inserted = energyStorage.insert(500.0, false);
 *
 * // Extract energy
 * double extracted = energyStorage.extract(200.0, false);
 *
 * // Save to NBT
 * PersistentDataContainer container = ...;
 * energyStorage.save(container);
 * }</pre>
 * <p>
 * <b>Usage Example - Fluid Storage:</b>
 * <pre>{@code
 * SimpleCapacity fluidTank = new SimpleCapacity(
 *     FluidUnit.MILLIBUCKET,
 *     16000.0,  // 16 buckets
 *     -20.0f,   // Min temp: -20°C
 *     100.0f,   // Max temp: 100°C
 *     true      // Can contain
 * );
 *
 * fluidTank.setTemperature(20.0f); // Room temperature
 *
 * // Check if temperature is valid
 * if (fluidTank.isTemperatureValid()) {
 *     fluidTank.insert(1000.0, false); // Insert 1 bucket
 * }
 * }</pre>
 *
 * @see Capacity
 * @see NBTSerializable
 */
public class SimpleCapacity implements Capacity, NBTSerializable {

    private final Unit unit;
    private final double maxCapacity;
    private final float minTemperature;
    private final float maxTemperature;
    private final boolean canContain;

    private double stored;
    private float temperature;
    private double maxInput;
    private double maxOutput;

    /**
     * Create a new capacity with default transfer rates.
     *
     * @param unit The unit type
     * @param maxCapacity Maximum storage capacity
     * @param minTemperature Minimum safe temperature (Celsius)
     * @param maxTemperature Maximum safe temperature (Celsius)
     * @param canContain Whether this can store permanently
     */
    public SimpleCapacity(
        Unit unit,
        double maxCapacity,
        float minTemperature,
        float maxTemperature,
        boolean canContain
    ) {
        this(unit, maxCapacity, minTemperature, maxTemperature, canContain,
             maxCapacity, maxCapacity); // Default: unlimited transfer rates
    }

    /**
     * Create a new capacity with custom transfer rates.
     *
     * @param unit The unit type
     * @param maxCapacity Maximum storage capacity
     * @param minTemperature Minimum safe temperature (Celsius)
     * @param maxTemperature Maximum safe temperature (Celsius)
     * @param canContain Whether this can store permanently
     * @param maxInput Maximum input per tick
     * @param maxOutput Maximum output per tick
     */
    public SimpleCapacity(
        Unit unit,
        double maxCapacity,
        float minTemperature,
        float maxTemperature,
        boolean canContain,
        double maxInput,
        double maxOutput
    ) {
        this.unit = Objects.requireNonNull(unit, "unit");
        this.maxCapacity = maxCapacity;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.canContain = canContain;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
        this.stored = 0.0;
        this.temperature = (minTemperature + maxTemperature) / 2; // Default to midpoint
    }

    /**
     * Default constructor for deserialization.
     * Must call {@link #load(PersistentDataContainer)} after construction.
     */
    public SimpleCapacity() {
        this.unit = null; // Will be set by load()
        this.maxCapacity = 0;
        this.minTemperature = 0;
        this.maxTemperature = 0;
        this.canContain = true;
        this.stored = 0;
        this.temperature = 0;
        this.maxInput = 0;
        this.maxOutput = 0;
    }

    // ==================== Capacity Interface ====================

    @Override
    public void put() {
        stored += 1d;
    }

    @Override
    public double get() {
        return stored;
    }

    @Override
    public double getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public boolean canContain() {
        return canContain;
    }

    @Override
    public float getMinTemperature() {
        return minTemperature;
    }

    @Override
    public float getMaxTemperature() {
        return maxTemperature;
    }

    @Override
    public float getInstantTemperature() {
        return temperature;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

    @Override
    public int setContainer() {
        return (int) Capacity.DEFAULT_MAX_CAPACITY;
    }

    @Override
    public void reset() {

    }

    @Override
    public void tick() {
        // Temperature decay towards ambient (optional)
        // Can be overridden by subclasses for specific behavior
    }

    @Override
    public double convertTo(Unit targetUnit) {
        if (this.unit == null || targetUnit == null) {
            return 0.0;
        }
        // TODO: Implement unit conversion when unit conversion system is ready
        // For now, return stored amount if same unit
        return this.unit.equals(targetUnit) ? stored : 0.0;
    }

    // ==================== Storage Operations ====================

    /**
     * Get the current stored amount.
     *
     * @return Current stored amount in this capacity's unit
     */
    public double getStored() {
        return stored;
    }

    /**
     * Get remaining capacity.
     *
     * @return How much more can be stored
     */
    public double getRemaining() {
        return maxCapacity - stored;
    }

    /**
     * Check if this capacity is empty.
     *
     * @return true if stored amount is 0
     */
    public boolean isEmpty() {
        return stored <= 0.0;
    }

    /**
     * Check if this capacity is full.
     *
     * @return true if stored amount equals max capacity
     */
    public boolean isFull() {
        return stored >= maxCapacity;
    }

    /**
     * Insert resource into this capacity.
     *
     * @param amount Amount to insert
     * @param simulate If true, don't actually modify the stored amount
     * @return Actual amount inserted
     */
    public double insert(double amount, boolean simulate) {
        if (!canContain || amount <= 0) {
            return 0.0;
        }

        double toInsert = Math.min(amount, getRemaining());
        toInsert = Math.min(toInsert, maxInput);

        if (!simulate) {
            stored += toInsert;
        }

        return toInsert;
    }

    /**
     * Extract resource from this capacity.
     *
     * @param amount Amount to extract
     * @param simulate If true, don't actually modify the stored amount
     * @return Actual amount extracted
     */
    public double extract(double amount, boolean simulate) {
        if (amount <= 0) {
            return 0.0;
        }

        double toExtract = Math.min(amount, stored);
        toExtract = Math.min(toExtract, maxOutput);

        if (!simulate) {
            stored -= toExtract;
        }

        return toExtract;
    }

    /**
     * Set the stored amount directly.
     * <p>
     * <b>Warning:</b> This bypasses transfer rate limits. Use for loading data only.
     *
     * @param amount The new stored amount
     */
    public void setStored(double amount) {
        this.stored = Math.max(0.0, Math.min(amount, maxCapacity));
    }

    /**
     * Set the current temperature.
     *
     * @param temperature Temperature in Celsius
     */
    @Override
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    /**
     * Get maximum input per tick.
     */
    public double getMaxInput() {
        return maxInput;
    }

    /**
     * Get maximum output per tick.
     */
    public double getMaxOutput() {
        return maxOutput;
    }

    /**
     * Set maximum input per tick.
     */
    public void setMaxInput(double maxInput) {
        this.maxInput = maxInput;
    }

    /**
     * Set maximum output per tick.
     */
    public void setMaxOutput(double maxOutput) {
        this.maxOutput = maxOutput;
    }

    // ==================== NBT Serialization ====================

    @Override
    public void save(PersistentDataContainer container) {
        NBTUtil.setInt(container, "capacity_version", 1);

        // Save unit info
        if (unit != null) {
            NBTUtil.setString(container, "unit_type", unit.getClass().getSimpleName());
            NBTUtil.setString(container, "unit_symbol", unit.getSymbol());
        }

        // Save capacity data
        NBTUtil.setDouble(container, "max_capacity", maxCapacity);
        NBTUtil.setDouble(container, "stored", stored);

        // Save temperature data
        NBTUtil.setFloat(container, "min_temp", minTemperature);
        NBTUtil.setFloat(container, "max_temp", maxTemperature);
        NBTUtil.setFloat(container, "current_temp", temperature);

        // Save transfer rates
        NBTUtil.setDouble(container, "max_input", maxInput);
        NBTUtil.setDouble(container, "max_output", maxOutput);

        // Save flags
        NBTUtil.setBoolean(container, "can_contain", canContain);
    }

    @Override
    public void load(PersistentDataContainer container) {
        int version = NBTUtil.getInt(container, "capacity_version", 1);

        if (version == 1) {
            loadV1(container);
        } else {
            throw new IllegalStateException("Unknown capacity data version: " + version);
        }
    }

    private void loadV1(PersistentDataContainer container) {
        // Load unit info (will need to be reconstructed by factory)
        // String unitType = NBTUtil.getString(container, "unit_type", "");
        // String unitSymbol = NBTUtil.getString(container, "unit_symbol", "");

        // Load stored amount
        this.stored = NBTUtil.getDouble(container, "stored", 0.0);

        // Load temperature
        this.temperature = NBTUtil.getFloat(container, "current_temp", 20.0f);

        // Note: Final fields (maxCapacity, minTemp, maxTemp, etc.) must be set by constructor
        // This load method is for runtime state only
    }

    @Override
    public String toString() {
        return String.format("Capacity{%s: %.2f/%.2f %s, temp: %.1f°C}",
            unit != null ? unit.getSymbol() : "?",
            stored,
            maxCapacity,
            unit != null ? unit.getSymbol() : "",
            temperature
        );
    }
}
