/**
 * Capability system for the Foundry framework.
 * <p>
 * The capability system allows machines and blocks to expose their features
 * in a type-safe, flexible way without requiring direct class dependencies.
 *
 * <h2>Core Concepts</h2>
 *
 * <h3>What is a Capability?</h3>
 * <p>
 * A capability is an <b>interface</b> that defines what a block/machine can do.
 * Examples:
 * <ul>
 *   <li><b>EnergyHandler:</b> Can store and transfer energy</li>
 *   <li><b>FluidHandler:</b> Can store and transfer fluids</li>
 *   <li><b>ItemHandler:</b> Can store and transfer items</li>
 *   <li><b>RecipeProcessor:</b> Can process recipes</li>
 * </ul>
 *
 * <h3>Why Use Capabilities?</h3>
 * <ul>
 *   <li><b>Decoupling:</b> Machines don't need to know each other's classes</li>
 *   <li><b>Flexibility:</b> A machine can provide multiple capabilities</li>
 *   <li><b>Directional:</b> Different capabilities on different sides</li>
 *   <li><b>Type Safe:</b> Compile-time type checking</li>
 *   <li><b>Extensible:</b> Plugins can define custom capabilities</li>
 * </ul>
 *
 * <h2>Architecture</h2>
 *
 * <pre>
 * ┌─────────────┐
 * │ Capability  │  (Marker interface)
 * └─────────────┘
 *       ▲
 *       │ implements
 *       │
 * ┌─────────────┐
 * │EnergyHandler│  (Specific capability interface)
 * └─────────────┘
 *       ▲
 *       │ implements
 *       │
 * ┌─────────────┐
 * │  Generator  │  (Machine implementation)
 * │             │
 * │ implements  │
 * │CapabilityProvider
 * └─────────────┘
 * </pre>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Defining a Custom Capability</h3>
 * <pre>{@code
 * // 1. Create the capability interface
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
 *
 * // 2. Create a capability key
 * public static final CapabilityKey<EnergyHandler> ENERGY =
 *     CapabilityKey.of("foundry", "energy", EnergyHandler.class);
 *
 * // 3. Implement the capability
 * public class SimpleEnergyStorage implements EnergyHandler {
 *
 *     private double stored;
 *     private final double capacity;
 *
 *     public SimpleEnergyStorage(double capacity) {
 *         this.capacity = capacity;
 *     }
 *
 *     @Override
 *     public double insert(double amount, boolean simulate) {
 *         double toInsert = Math.min(amount, capacity - stored);
 *         if (!simulate) {
 *             stored += toInsert;
 *         }
 *         return toInsert;
 *     }
 *
 *     @Override
 *     public double extract(double amount, boolean simulate) {
 *         double toExtract = Math.min(amount, stored);
 *         if (!simulate) {
 *             stored -= toExtract;
 *         }
 *         return toExtract;
 *     }
 *
 *     @Override
 *     public double getStored() {
 *         return stored;
 *     }
 *
 *     @Override
 *     public double getCapacity() {
 *         return capacity;
 *     }
 * }
 * }</pre>
 *
 * <h3>Providing Capabilities</h3>
 * <pre>{@code
 * public class Generator implements CapabilityProvider {
 *
 *     private final EnergyHandler energyStorage =
 *         new SimpleEnergyStorage(10000);
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
 * <h3>Using SimpleCapabilityProvider</h3>
 * <pre>{@code
 * public class ElectricFurnace extends SimpleCapabilityProvider {
 *
 *     private final EnergyStorage energy = new SimpleEnergyStorage(5000);
 *     private final ItemInventory items = new SimpleItemInventory(3);
 *
 *     public ElectricFurnace() {
 *         // Energy on all sides
 *         registerCapability(FoundryCapabilities.ENERGY, energy);
 *
 *         // Items only from top and bottom
 *         registerCapability(
 *             FoundryCapabilities.ITEM,
 *             items,
 *             Direction.UP,
 *             Direction.DOWN
 *         );
 *     }
 * }
 * }</pre>
 *
 * <h3>Querying Capabilities</h3>
 * <pre>{@code
 * // Get a machine at a location
 * Optional<BlockInstance> block = blockManager.getBlock(location);
 * if (block.isEmpty()) return;
 *
 * // Check if it's a capability provider
 * if (!(block.get() instanceof CapabilityProvider provider)) {
 *     return;
 * }
 *
 * // Query energy capability from the top
 * Optional<EnergyHandler> energy =
 *     provider.getCapability(FoundryCapabilities.ENERGY, Direction.UP);
 *
 * if (energy.isPresent()) {
 *     // Insert 1000 EU
 *     double inserted = energy.get().insert(1000.0, false);
 *     System.out.println("Inserted " + inserted + " EU");
 * }
 * }</pre>
 *
 * <h3>Directional Capabilities</h3>
 * <pre>{@code
 * public class Pump extends SimpleCapabilityProvider {
 *
 *     private final FluidTank inputTank = new SimpleFluidTank(16000);
 *     private final FluidTank outputTank = new SimpleFluidTank(16000);
 *
 *     public Pump() {
 *         // Different tanks on different sides
 *         Map<Direction, FluidHandler> fluidHandlers = Map.of(
 *             Direction.DOWN, inputTank,   // Input from below
 *             Direction.UP, outputTank      // Output above
 *         );
 *
 *         registerCapability(FoundryCapabilities.FLUID, fluidHandlers);
 *     }
 * }
 * }</pre>
 *
 * <h2>Capability Query Rules</h2>
 *
 * <h3>Direction Perspective</h3>
 * <p>
 * The direction parameter represents the <b>accessor's perspective</b>, not the provider's.
 * <p>
 * Example: If a pipe is NORTH of a machine, the pipe queries the machine's SOUTH side:
 * <pre>
 *     [Pipe]
 *       ↓ (queries SOUTH)
 *   [Machine]
 * </pre>
 *
 * <h3>Query Requirements</h3>
 * <ol>
 *   <li>Queries must NOT modify provider state</li>
 *   <li>Returned capabilities must NOT expose mutable internals</li>
 *   <li>Queries must be thread-safe</li>
 *   <li>The same instance may be returned for multiple sides</li>
 * </ol>
 *
 * <h3>Lifecycle</h3>
 * <ul>
 *   <li>Capabilities are created when the provider is instantiated</li>
 *   <li>Capability references should not be cached long-term</li>
 *   <li>Re-query capabilities if the provider might have changed</li>
 *   <li>Capabilities become invalid when the provider is removed</li>
 * </ul>
 *
 * <h2>Best Practices</h2>
 *
 * <h3>✅ Do This</h3>
 * <ul>
 *   <li>Define capabilities as interfaces</li>
 *   <li>Use immutable data in capability methods</li>
 *   <li>Validate all inputs</li>
 *   <li>Use simulate mode before actual operations</li>
 *   <li>Cache capability keys, not capability instances</li>
 *   <li>Return empty Optional if capability is not supported</li>
 * </ul>
 *
 * <h3>❌ Don't Do This</h3>
 * <ul>
 *   <li>Don't expose mutable collections</li>
 *   <li>Don't modify state during queries</li>
 *   <li>Don't cache capability instances across ticks</li>
 *   <li>Don't assume all machines have all capabilities</li>
 *   <li>Don't use capabilities for world access from async threads</li>
 * </ul>
 *
 * <h2>Compliance with PLAN.md</h2>
 *
 * This implementation follows PLAN.md Section 7:
 * <ul>
 *   <li>✅ Capability abstraction allows plugin interoperability</li>
 *   <li>✅ Machines don't depend on concrete types</li>
 *   <li>✅ Direction-aware capability queries</li>
 *   <li>✅ No state modification during queries</li>
 *   <li>✅ Capabilities have clear lifecycle</li>
 *   <li>✅ Support for dynamic capability changes</li>
 * </ul>
 *
 * @see cn.epicmc.foundry.api.capability.Capability
 * @see cn.epicmc.foundry.api.capability.CapabilityProvider
 * @see cn.epicmc.foundry.api.capability.CapabilityKey
 * @see cn.epicmc.foundry.api.capability.Direction
 */
package cn.epicmc.foundry.api.capability;
