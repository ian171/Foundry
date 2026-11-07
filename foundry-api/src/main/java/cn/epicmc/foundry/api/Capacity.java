package cn.epicmc.foundry.api;

import cn.epicmc.foundry.api.unit.Unit;

public interface Capacity {

    /**
     * Get the max amount of things that can be stored.
     * @return amount
     */
    double getMaxCapacity();

    /**
     * Can store things permanently or leak.
     * @return if it can be contained permanently.
     */
    boolean canContain();

    /**
     * Get the lowest temperature in Celsius.
     * @return temperature in float
     */
    float getMinTemperature();

    /**
     * Get the highest temperature in Celsius.
     * @return temperature in float
     */
    float getMaxTemperature();

    /**
     * Get temperature of container.
     * @return
     */
    float getInstantTemperature();

    Unit getUnit();
    /**
     * Is the temperature of the container in the range of Max and min temperature.
     * @return
     */
    default boolean isTemperatureValid(){
        return getInstantTemperature() > getMinTemperature() && getInstantTemperature() < getMaxTemperature();
    }

    /**
     * Run in each tick.
     */
    void tick();


}
