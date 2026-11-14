package cn.epicmc.foundry.common.capacity;

import cn.epicmc.foundry.api.Capacity;
import cn.epicmc.foundry.api.unit.FluidUnit;
import cn.epicmc.foundry.api.unit.Unit;
import cn.epicmc.foundry.common.nbt.NBTSerializable;
import cn.epicmc.foundry.common.nbt.NBTUtil;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Objects;

/**
 * Fluid storage implementation with temperature and containment tracking.
 * <p>
 * This capacity type stores a single fluid type with amount tracking,
 * temperature management, and containment validation.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Single fluid type per tank</li>
 *   <li>Temperature range validation</li>
 *   <li>Transfer rate limits (input/output per tick)</li>
 *   <li>Leak detection when temperature out of range</li>
 *   <li>NBT persistence</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Create water tank (16 buckets = 16000 mB)
 * FluidCapacity waterTank = new FluidCapacity(
 *     FluidUnit.MILLIBUCKET,
 *     16000.0,
 *     -10.0f,  // Can freeze
 *     100.0f,  // Can boil
 *     Material.WATER
 * );
 *
 * // Fill tank
 * double filled = waterTank.fill(1000.0, false); // 1 bucket
 *
 * // Check temperature
 * waterTank.setTemperature(95.0f);
 * if (waterTank.isTemperatureValid()) {
 *     // Safe to operate
 * }
 *
 * // Drain fluid
 * double drained = waterTank.drain(500.0, false);
 *
 * // Save to NBT
 * PersistentDataContainer container = ...;
 * waterTank.save(container);
 * }</pre>
 */
public class FluidCapacity implements Capacity, NBTSerializable {

    private final Unit unit;
    private final double maxCapacity;
    private final float minTemperature;
    private final float maxTemperature;
    private final boolean canContain;

    private Material fluidType;
    private double stored;
    private float temperature;
    private double maxInput;
    private double maxOutput;

    /**
     * Create fluid capacity for a specific fluid type.
     *
     * @param unit Fluid unit (e.g., MILLIBUCKET)
     * @param maxCapacity Maximum storage in mB
     * @param minTemperature Minimum safe temperature (°C)
     * @param maxTemperature Maximum safe temperature (°C)
     * @param fluidType The fluid material type
     */
    public FluidCapacity(
        Unit unit,
        double maxCapacity,
        float minTemperature,
        float maxTemperature,
        Material fluidType
    ) {
        this(unit, maxCapacity, minTemperature, maxTemperature, fluidType, true);
    }

    /**
     * Create fluid capacity with containment flag.
     */
    public FluidCapacity(
        Unit unit,
        double maxCapacity,
        float minTemperature,
        float maxTemperature,
        Material fluidType,
        boolean canContain
    ) {
        this.unit = Objects.requireNonNull(unit, "unit");
        this.maxCapacity = maxCapacity;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.fluidType = fluidType;
        this.canContain = canContain;
        this.stored = 0.0;
        this.temperature = (minTemperature + maxTemperature) / 2;
        this.maxInput = maxCapacity;
        this.maxOutput = maxCapacity;
    }

    /**
     * Default constructor for deserialization.
     */
    public FluidCapacity() {
        this.unit = null;
        this.maxCapacity = 0;
        this.minTemperature = 0;
        this.maxTemperature = 0;
        this.fluidType = Material.WATER;
        this.canContain = true;
        this.stored = 0;
        this.temperature = 20;
        this.maxInput = 0;
        this.maxOutput = 0;
    }

    // ==================== Capacity Interface ====================

    @Override
    public void put() {
        stored +=1;
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
        return canContain && isTemperatureValid();
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
        return 0;
    }

    @Override
    public void reset() {
        setStored(0d);
    }

    @Override
    public void tick() {
        // Temperature-based leaking
        if (!isTemperatureValid() && stored > 0) {
            // Leak 1% per tick when temperature out of range
            double leakAmount = stored * 0.01;
            stored = Math.max(0, stored - leakAmount);
        }
    }

    @Override
    public double convertTo(Unit targetUnit) {
        if (this.unit == null || targetUnit == null) {
            return 0.0;
        }
        // TODO: Implement unit conversion
        return this.unit.equals(targetUnit) ? stored : 0.0;
    }

    // ==================== Fluid Operations ====================

    /**
     * Get the fluid type stored in this tank.
     */
    public Material getFluidType() {
        return fluidType;
    }

    /**
     * Set the fluid type.
     * <p>
     * <b>Warning:</b> This clears the tank if fluid type changes.
     */
    public void setFluidType(Material fluidType) {
        if (this.fluidType != fluidType) {
            this.stored = 0; // Clear tank when changing fluid
        }
        this.fluidType = fluidType;
    }

    /**
     * Get current stored amount.
     */
    public double getStored() {
        return stored;
    }

    /**
     * Get remaining capacity.
     */
    public double getRemaining() {
        return maxCapacity - stored;
    }

    /**
     * Check if tank is empty.
     */
    public boolean isEmpty() {
        return stored <= 0.0;
    }

    /**
     * Check if tank is full.
     */
    public boolean isFull() {
        return stored >= maxCapacity;
    }

    /**
     * Fill fluid into tank.
     *
     * @param fluidType Fluid type to fill
     * @param amount Amount to fill
     * @param simulate If true, don't modify tank
     * @return Actual amount filled
     */
    public double fill(Material fluidType, double amount, boolean simulate) {
        if (!canContain() || amount <= 0) {
            return 0.0;
        }

        // Empty tank - accept any fluid
        if (isEmpty()) {
            double toFill = Math.min(amount, maxCapacity);
            toFill = Math.min(toFill, maxInput);

            if (!simulate) {
                this.fluidType = fluidType;
                this.stored = toFill;
            }
            return toFill;
        }

        // Tank has fluid - must match type
        if (this.fluidType != fluidType) {
            return 0.0; // Can't mix fluids
        }

        double toFill = Math.min(amount, getRemaining());
        toFill = Math.min(toFill, maxInput);

        if (!simulate) {
            stored += toFill;
        }

        return toFill;
    }

    /**
     * Fill fluid (uses current fluid type).
     */
    public double fill(double amount, boolean simulate) {
        return fill(this.fluidType, amount, simulate);
    }

    /**
     * Drain fluid from tank.
     *
     * @param amount Amount to drain
     * @param simulate If true, don't modify tank
     * @return Actual amount drained
     */
    public double drain(double amount, boolean simulate) {
        if (isEmpty() || amount <= 0) {
            return 0.0;
        }

        double toDrain = Math.min(amount, stored);
        toDrain = Math.min(toDrain, maxOutput);

        if (!simulate) {
            stored -= toDrain;
            if (stored <= 0) {
                stored = 0;
                // Keep fluidType for next fill
            }
        }

        return toDrain;
    }

    /**
     * Set temperature.
     */
    @Override
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    /**
     * Set stored amount directly (for loading data).
     */
    public void setStored(double amount) {
        this.stored = Math.max(0.0, Math.min(amount, maxCapacity));
    }

    // ==================== NBT Serialization ====================

    @Override
    public void save(PersistentDataContainer container) {
        NBTUtil.setInt(container, "fluid_capacity_version", 1);

        // Save unit info
        if (unit != null) {
            NBTUtil.setString(container, "unit_symbol", unit.getSymbol());
        }

        // Save capacity data
        NBTUtil.setDouble(container, "max_capacity", maxCapacity);
        NBTUtil.setDouble(container, "stored", stored);

        // Save fluid type
        if (fluidType != null) {
            NBTUtil.setString(container, "fluid_type", fluidType.name());
        }

        // Save temperature
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
        int version = NBTUtil.getInt(container, "fluid_capacity_version", 1);

        switch (version) {
            case 1 -> loadV1(container);
            default -> throw new IllegalStateException("Unknown fluid capacity version: " + version);
        }
    }

    private void loadV1(PersistentDataContainer container) {
        // Load stored amount
        this.stored = NBTUtil.getDouble(container, "stored", 0.0);

        // Load fluid type
        String fluidName = NBTUtil.getString(container, "fluid_type", "WATER");
        try {
            this.fluidType = Material.valueOf(fluidName);
        } catch (IllegalArgumentException e) {
            this.fluidType = Material.WATER;
        }

        // Load temperature
        this.temperature = NBTUtil.getFloat(container, "current_temp", 20.0f);
    }

    @Override
    public String toString() {
        return String.format("FluidCapacity{%s: %.2f/%.2f %s, temp: %.1f°C}",
            fluidType != null ? fluidType.name() : "EMPTY",
            stored,
            maxCapacity,
            unit != null ? unit.getSymbol() : "mB",
            temperature
        );
    }
}
