package cn.epicmc.foundry.api;

import cn.epicmc.foundry.api.unit.Unit;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface Capacity {
    double DEFAULT_MAX_CAPACITY = 64d;

    void put();

    double get();

    double getMaxCapacity();

    boolean canContain();

    float getMinTemperature();
    float getMaxTemperature();
    float getInstantTemperature();
    void setTemperature(float temperature);
    void tick();
    Unit getUnit();
    int setContainer();

    default boolean isTemperatureValid(){
        return getInstantTemperature() < getMaxTemperature() && getInstantTemperature() > getMinTemperature();
    }
    void reset();

    /**
     * Convert one unit to another unit. Accept all unit from cn.epicmc.foundry.api.unit
     * @param unit
     * @return
     */
    double convertTo(Unit unit);


}
