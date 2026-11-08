/**
 * Annotation system for the Foundry framework.
 * <p>
 * This package provides declarative annotations that serve as syntax sugar
 * for the Registry system. Annotations make code more readable and reduce
 * boilerplate, but they are <b>not a replacement</b> for the Registry.
 *
 * <h2>Design Philosophy</h2>
 *
 * <blockquote>
 * <b>"Annotations are an entry point to Registry, not a replacement."</b>
 * </blockquote>
 *
 * <p>
 * The annotation system is syntactic sugar that:
 * <ul>
 *   <li>Makes code more declarative and self-documenting</li>
 *   <li>Reduces registration boilerplate</li>
 *   <li>Provides compile-time validation</li>
 *   <li>Enables automatic documentation generation</li>
 * </ul>
 *
 * <p>
 * Under the hood, annotations are processed during the REGISTRY phase and
 * transformed into proper Registry calls. The Registry remains the source
 * of truth for all registered content.
 *
 * <h2>Core Annotations</h2>
 *
 * <h3>Identity Annotation</h3>
 * <p>
 * {@link cn.epicmc.foundry.api.annotations.Foundry} - Every Foundry content
 * must have a unique identity:
 * <pre>{@code
 * @Foundry(namespace = "example", id = "copper_generator")
 * public class CopperGenerator extends AbstractBlock {
 *     // ...
 * }
 * }</pre>
 *
 * <h3>Block Annotations</h3>
 * <p>
 * {@link cn.epicmc.foundry.api.annotations.FoundryBlock} - Define block properties:
 * <pre>{@code
 * @FoundryBlock(
 *     material = Material.NOTE_BLOCK,
 *     hardness = 5.0f,
 *     resistance = 6.0f,
 *     isMachine = true
 * )
 * }</pre>
 *
 * <h3>Item Annotations</h3>
 * <p>
 * {@link cn.epicmc.foundry.api.annotations.FoundryItem} - Define item properties:
 * <pre>{@code
 * @FoundryItem(
 *     material = Material.IRON_INGOT,
 *     customModelData = 100001,
 *     maxStackSize = 64
 * )
 * }</pre>
 *
 * <h3>Machine Annotations</h3>
 * <p>
 * {@link cn.epicmc.foundry.api.annotations.FoundryMachine} - Define machine behavior:
 * <pre>{@code
 * @FoundryMachine(
 *     tickInterval = 20,
 *     alwaysTick = false,
 *     processesRecipes = true
 * )
 * }</pre>
 *
 * <h3>Capability Annotations</h3>
 * <p>
 * {@link cn.epicmc.foundry.api.annotations.ProvideCapability} - Declare provided capabilities:
 * <pre>{@code
 * @ProvideCapability(EnergyHandler.class)
 * @ProvideCapability(FluidHandler.class)
 * @ProvideCapability(ItemHandler.class)
 * }</pre>
 *
 * <p>
 * {@link cn.epicmc.foundry.api.annotations.RequireCapability} - Declare required capabilities:
 * <pre>{@code
 * @RequireCapability(value = EnergyHandler.class, optional = false)
 * }</pre>
 *
 * <h3>Event Annotations</h3>
 * <p>
 * Event handling through method annotations:
 * <ul>
 *   <li>{@link cn.epicmc.foundry.api.annotations.event.OnPlace}</li>
 *   <li>{@link cn.epicmc.foundry.api.annotations.event.OnBreak}</li>
 *   <li>{@link cn.epicmc.foundry.api.annotations.event.OnInteract}</li>
 *   <li>{@link cn.epicmc.foundry.api.annotations.event.OnExplode}</li>
 * </ul>
 *
 * <h2>Complete Example</h2>
 *
 * <pre>{@code
 * @Foundry(
 *     namespace = "example",
 *     id = "electric_furnace",
 *     name = "Electric Furnace"
 * )
 * @FoundryBlock(
 *     material = Material.NOTE_BLOCK,
 *     hardness = 5.0f,
 *     resistance = 6.0f,
 *     isMachine = true
 * )
 * @FoundryMachine(
 *     tickInterval = 20,
 *     processesRecipes = true
 * )
 * @ProvideCapability(EnergyHandler.class)
 * @ProvideCapability(ItemHandler.class)
 * @RequireCapability(value = EnergyHandler.class, optional = false)
 * public class ElectricFurnace extends AbstractMachine {
 *
 *     private final EnergyStorage energyStorage =
 *         new SimpleEnergyStorage(5000);
 *     private final ItemInventory inventory =
 *         new SimpleItemInventory(3);
 *
 *     @OnPlace
 *     public void onPlace(PlacementContext context) {
 *         context.getPlayer().ifPresent(player -> {
 *             player.sendMessage("§aElectric Furnace placed!");
 *         });
 *     }
 *
 *     @OnBreak
 *     public boolean onBreak(Player player) {
 *         if (energyStorage.getStored() > 0) {
 *             player.sendMessage("§cFurnace still has energy!");
 *             return false; // Cancel break
 *         }
 *         return true;
 *     }
 *
 *     @OnInteract
 *     public boolean onInteract(Player player, Action action) {
 *         if (action == Action.RIGHT_CLICK_BLOCK) {
 *             openGUI(player);
 *             return true;
 *         }
 *         return false;
 *     }
 *
 *     @Override
 *     public <T extends Capability> Optional<T> getCapability(
 *         CapabilityKey<T> key,
 *         Direction side
 *     ) {
 *         if (key == FoundryCapabilities.ENERGY) {
 *             return Optional.of(key.getType().cast(energyStorage));
 *         }
 *         if (key == FoundryCapabilities.ITEM) {
 *             return Optional.of(key.getType().cast(inventory));
 *         }
 *         return Optional.empty();
 *     }
 * }
 * }</pre>
 *
 * <h2>Annotation Processing</h2>
 *
 * <p>
 * Annotations are processed during plugin initialization:
 *
 * <pre>
 * Plugin Enable
 *      ↓
 * Scan Classpath
 *      ↓
 * Find @Foundry Classes
 *      ↓
 * Process Annotations
 *      ↓
 * Generate Registry Calls
 *      ↓
 * Register in REGISTRY Phase
 *      ↓
 * Normal Operation
 * </pre>
 *
 * <h2>Annotation vs Registry</h2>
 *
 * <table border="1">
 *   <tr>
 *     <th>Aspect</th>
 *     <th>Annotations</th>
 *     <th>Direct Registry</th>
 *   </tr>
 *   <tr>
 *     <td>Ease of Use</td>
 *     <td>✅ Simple, declarative</td>
 *     <td>⚠️ More verbose</td>
 *   </tr>
 *   <tr>
 *     <td>Flexibility</td>
 *     <td>⚠️ Limited to annotation params</td>
 *     <td>✅ Full control</td>
 *   </tr>
 *   <tr>
 *     <td>Type Safety</td>
 *     <td>✅ Compile-time checks</td>
 *     <td>✅ Type-safe at call site</td>
 *   </tr>
 *   <tr>
 *     <td>Documentation</td>
 *     <td>✅ Self-documenting</td>
 *     <td>⚠️ Requires separate docs</td>
 *   </tr>
 *   <tr>
 *     <td>Dynamic Registration</td>
 *     <td>❌ Static only</td>
 *     <td>✅ Can be dynamic</td>
 *   </tr>
 * </table>
 *
 * <h2>Best Practices</h2>
 *
 * <ol>
 *   <li><b>Use annotations for static content:</b> Blocks, items, machines</li>
 *   <li><b>Use Registry for dynamic content:</b> Runtime-generated definitions</li>
 *   <li><b>Don't mix approaches:</b> Stick to one style per plugin</li>
 *   <li><b>Annotations are documentation:</b> Keep them clear and complete</li>
 *   <li><b>Complex behavior in code:</b> Annotations declare, code implements</li>
 * </ol>
 *
 * <h2>Migration</h2>
 *
 * <h3>From Registry to Annotations</h3>
 * <pre>{@code
 * // Before: Manual registration
 * public void onEnable() {
 *     MutableRegistry<BlockDefinition> registry =
 *         FoundryAPI.getRegistryManager()
 *                   .getMutableRegistry(FoundryRegistries.BLOCKS);
 *
 *     BlockDefinition generator = new Generator(
 *         new NamespacedKey(this, "generator"),
 *         Material.NOTE_BLOCK,
 *         "Generator"
 *     );
 *
 *     registry.register(generator.getId(), generator);
 * }
 *
 * // After: Annotation-based
 * @Foundry(namespace = "example", id = "generator")
 * @FoundryBlock(material = Material.NOTE_BLOCK)
 * public class Generator extends AbstractBlock {
 *     // Automatically registered
 * }
 * }</pre>
 *
 * @see cn.epicmc.foundry.api.annotations.Foundry
 * @see cn.epicmc.foundry.api.registry.Registry
 */
package cn.epicmc.foundry.api.annotations;
