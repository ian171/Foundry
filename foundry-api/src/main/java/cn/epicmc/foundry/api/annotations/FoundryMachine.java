package cn.epicmc.foundry.api.annotations;

import java.lang.annotation.*;

/**
 * Marks a class as a Foundry machine and defines its tick behavior.
 * <p>
 * Machines are special blocks that:
 * <ul>
 *   <li>Tick (update every N game ticks)</li>
 *   <li>Process recipes</li>
 *   <li>Have capabilities (energy, fluids, items)</li>
 *   <li>Store persistent state</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * @Foundry(namespace = "example", id = "electric_furnace")
 * @FoundryBlock(material = Material.NOTE_BLOCK, isMachine = true)
 * @FoundryMachine(tickInterval = 20)
 * @ProvideCapability(EnergyHandler.class)
 * @ProvideCapability(ItemHandler.class)
 * public class ElectricFurnace extends AbstractMachine {
 *     // ...
 * }
 * }</pre>
 *
 * @see Foundry
 * @see FoundryBlock
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FoundryMachine {

    /**
     * How often this machine ticks (in game ticks).
     * <p>
     * Examples:
     * <ul>
     *   <li>1 tick - Every tick (20 times per second)</li>
     *   <li>5 ticks - 4 times per second</li>
     *   <li>20 ticks - Once per second</li>
     * </ul>
     * <p>
     * Lower values mean higher update frequency but more server load.
     *
     * @return The tick interval in game ticks
     */
    int tickInterval() default 20;

    /**
     * Whether this machine should tick even when no players are nearby.
     * <p>
     * If false, the machine will pause when no players are in range.
     * This can save performance for non-critical machines.
     *
     * @return {@code true} to always tick
     */
    boolean alwaysTick() default false;

    /**
     * Whether this machine processes recipes.
     *
     * @return {@code true} if this is a recipe processor
     */
    boolean processesRecipes() default false;
}
