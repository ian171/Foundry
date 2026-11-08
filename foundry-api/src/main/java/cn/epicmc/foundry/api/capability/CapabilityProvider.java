package cn.epicmc.foundry.api.capability;

import java.util.Optional;
import java.util.Set;

/**
 * An object that can provide capabilities.
 * <p>
 * Capability providers expose their supported capabilities through this interface.
 * This allows different plugins to interact with machines without knowing their
 * concrete implementation.
 * <p>
 * <b>Example Usage:</b>
 * <pre>{@code
 * // Check if a machine can store energy
 * Optional<EnergyHandler> handler =
 *     machine.getCapability(FoundryCapabilities.ENERGY, Direction.UP);
 *
 * if (handler.isPresent()) {
 *     double inserted = handler.get().insert(1000.0, false);
 *     System.out.println("Inserted " + inserted + " EU");
 * }
 * }</pre>
 * <p>
 * <b>Implementing a Provider:</b>
 * <pre>{@code
 * public class Generator implements CapabilityProvider {
 *
 *     private final EnergyStorage energyStorage = new SimpleEnergyStorage(10000);
 *
 *     @Override
 *     public <T extends Capability> Optional<T> getCapability(
 *         CapabilityKey<T> key,
 *         Direction side
 *     ) {
 *         // Provide energy capability on all sides
 *         if (key == FoundryCapabilities.ENERGY) {
 *             return Optional.of(key.getType().cast(energyStorage));
 *         }
 *         return Optional.empty();
 *     }
 *
 *     @Override
 *     public Set<CapabilityKey<?>> getCapabilities() {
 *         return Set.of(FoundryCapabilities.ENERGY);
 *     }
 * }
 * }</pre>
 *
 * @see Capability
 * @see CapabilityKey
 */
public interface CapabilityProvider {

    /**
     * Get a capability from this provider.
     * <p>
     * This method is the primary way to query capabilities. It should be
     * fast and thread-safe for read operations.
     * <p>
     * <b>Important Rules:</b>
     * <ul>
     *   <li>Queries should NOT modify provider state</li>
     *   <li>Returned capabilities should NOT expose mutable internals</li>
     *   <li>The same capability instance may be returned for multiple sides</li>
     *   <li>Direction represents the accessor's perspective</li>
     * </ul>
     * <p>
     * <b>Thread Safety:</b> This method may be called from any thread.
     * The returned capability must be safe to use from the calling thread,
     * but world access should be done through proper scheduling.
     *
     * @param key The capability key to query
     * @param side The side being accessed
     * @param <T> The capability type
     * @return The capability if supported, or empty
     */
    <T extends Capability> Optional<T> getCapability(CapabilityKey<T> key, Direction side);

    /**
     * Get all capabilities supported by this provider.
     * <p>
     * This returns the set of all capability keys that this provider can provide,
     * regardless of direction. The actual capability may not be available on all sides.
     * <p>
     * This is primarily used for:
     * <ul>
     *   <li>UI display of supported features</li>
     *   <li>Documentation generation</li>
     *   <li>Network discovery</li>
     * </ul>
     * <p>
     * The returned set should be immutable and cached when possible.
     *
     * @return An immutable set of supported capability keys
     */
    Set<CapabilityKey<?>> getCapabilities();

    /**
     * Check if this provider supports a specific capability on a given side.
     * <p>
     * This is a convenience method that checks if the capability would be available.
     *
     * @param key The capability key to check
     * @param side The side to check
     * @param <T> The capability type
     * @return {@code true} if the capability is supported, {@code false} otherwise
     */
    default <T extends Capability> boolean hasCapability(CapabilityKey<T> key, Direction side) {
        return getCapability(key, side).isPresent();
    }

    /**
     * Check if this provider supports a capability on any side.
     * <p>
     * This checks if the capability is available from at least one direction.
     *
     * @param key The capability key to check
     * @return {@code true} if the capability is supported on any side
     */
    default boolean hasCapability(CapabilityKey<?> key) {
        return getCapabilities().contains(key);
    }
}
