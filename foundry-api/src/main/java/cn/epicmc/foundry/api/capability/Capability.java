package cn.epicmc.foundry.api.capability;

/**
 * Marker interface for all capability types.
 * <p>
 * Capabilities represent behaviors or services that a block, machine, or entity
 * can provide. Common capabilities include:
 * <ul>
 *   <li><b>EnergyHandler:</b> Can store/transfer energy</li>
 *   <li><b>FluidHandler:</b> Can store/transfer fluids</li>
 *   <li><b>ItemHandler:</b> Can store/transfer items</li>
 *   <li><b>HeatHandler:</b> Has temperature and heat capacity</li>
 *   <li><b>RecipeProcessor:</b> Can process recipes</li>
 * </ul>
 * <p>
 * <b>Design Principles:</b>
 * <ul>
 *   <li>Capabilities are <b>interfaces</b>, not classes</li>
 *   <li>Capabilities define <b>what can be done</b>, not how</li>
 *   <li>Machines implement capabilities they support</li>
 *   <li>Capabilities can be <b>directional</b> (different per side)</li>
 * </ul>
 * <p>
 * <b>Example Capability:</b>
 * <pre>{@code
 * public interface EnergyHandler extends Capability {
 *
 *     double insert(double amount, boolean simulate);
 *
 *     double extract(double amount, boolean simulate);
 *
 *     double getStored();
 *
 *     double getCapacity();
 * }
 * }</pre>
 *
 * @see CapabilityProvider
 * @see CapabilityKey
 */
public interface Capability {
    // Marker interface - no methods required
}
