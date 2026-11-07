/**
 * Registry system for the Foundry framework.
 * <p>
 * The registry system provides a centralized, type-safe way to register and look up
 * extensible objects like machines, fluids, energy types, and recipes.
 *
 * <h2>Core Concepts</h2>
 *
 * <h3>Registry</h3>
 * <p>
 * A registry is a collection of objects identified by unique {@link org.bukkit.NamespacedKey}s.
 * Registries are read-only during runtime and can only be modified during specific phases.
 *
 * <h3>Registration Phases</h3>
 * <p>
 * The framework progresses through lifecycle phases:
 * <ol>
 *   <li><b>BOOTSTRAP</b> - Core initialization</li>
 *   <li><b>REGISTRY</b> - Plugin registration (plugins register here)</li>
 *   <li><b>WORLD_LOAD</b> - Load world data</li>
 *   <li><b>RUNTIME</b> - Normal operation (registries frozen)</li>
 *   <li><b>SHUTDOWN</b> - Cleanup</li>
 * </ol>
 *
 * <h3>Static vs Dynamic Registration</h3>
 * <p>
 * <b>Static Registration:</b> Most registries only accept entries during the REGISTRY phase.
 * Once the server enters RUNTIME, these registries are frozen.
 * <p>
 * <b>Dynamic Registration:</b> Some registries support adding entries at runtime.
 * These registries must explicitly handle unload and cleanup operations.
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Registering During Plugin Load</h3>
 * <pre>{@code
 * public class MyPlugin extends JavaPlugin {
 *
 *     @Override
 *     public void onEnable() {
 *         // Get the mutable registry during REGISTRY phase
 *         MutableRegistry<EnergyUnit> registry =
 *             FoundryAPI.getRegistryManager()
 *                       .getMutableRegistry(FoundryRegistries.ENERGY_UNITS);
 *
 *         // Create and register your energy unit
 *         EnergyUnit myUnit = new MyEnergyUnit();
 *         registry.register(
 *             new NamespacedKey(this, "my_energy"),
 *             myUnit
 *         );
 *     }
 * }
 * }</pre>
 *
 * <h3>Looking Up Registered Objects</h3>
 * <pre>{@code
 * // Get a read-only registry (works anytime after registration)
 * Registry<EnergyUnit> registry =
 *     FoundryAPI.getRegistryManager()
 *               .getRegistry(FoundryRegistries.ENERGY_UNITS);
 *
 * // Look up by key
 * Optional<EnergyUnit> unit = registry.get(
 *     new NamespacedKey("example", "my_energy")
 * );
 *
 * // Iterate all registered units
 * for (EnergyUnit unit : registry.values()) {
 *     System.out.println(unit.getName());
 * }
 * }</pre>
 *
 * <h3>Handling Registration Errors</h3>
 * <pre>{@code
 * try {
 *     registry.register(key, value);
 * } catch (RegistrationException e) {
 *     // Key is already registered
 *     getLogger().warning("Failed to register: " + e.getMessage());
 * } catch (RegistryFrozenException e) {
 *     // Tried to register outside REGISTRY phase
 *     getLogger().severe("Registry is frozen: " + e.getMessage());
 * }
 * }</pre>
 *
 * <h3>Creating Custom Registry Keys</h3>
 * <pre>{@code
 * public class MyRegistries {
 *     public static final RegistryKey<MyCustomType> MY_TYPES =
 *         RegistryKey.of("myplugin", "my_type", MyCustomType.class);
 * }
 *
 * // Request Foundry to create your registry during bootstrap
 * // (This would be done by Foundry or through a registration API)
 * }</pre>
 *
 * <h2>Best Practices</h2>
 * <ol>
 *   <li>Always use {@link org.bukkit.NamespacedKey} with your plugin namespace</li>
 *   <li>Register during onEnable() in the REGISTRY phase</li>
 *   <li>Never try to register the same key twice</li>
 *   <li>Cache registry references instead of looking them up repeatedly</li>
 *   <li>Use type-safe {@link cn.epicmc.foundry.api.registry.RegistryKey} constants</li>
 *   <li>Handle registration exceptions gracefully</li>
 *   <li>Don't rely on registration order between plugins</li>
 * </ol>
 *
 * <h2>Anti-Patterns</h2>
 * <ul>
 *   <li>❌ Registering in static initializers</li>
 *   <li>❌ Trying to modify registries during RUNTIME</li>
 *   <li>❌ Catching exceptions and re-registering with a different key</li>
 *   <li>❌ Using string concatenation instead of NamespacedKey</li>
 *   <li>❌ Storing mutable state in registered objects</li>
 * </ul>
 *
 * @see cn.epicmc.foundry.api.registry.Registry
 * @see cn.epicmc.foundry.api.registry.MutableRegistry
 * @see cn.epicmc.foundry.api.registry.RegistryManager
 * @see cn.epicmc.foundry.api.registry.RegistryKey
 * @see cn.epicmc.foundry.api.registry.FoundryRegistries
 */
package cn.epicmc.foundry.api.registry;
