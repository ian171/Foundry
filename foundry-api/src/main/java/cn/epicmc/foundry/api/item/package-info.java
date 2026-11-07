/**
 * Item system for the Foundry framework.
 * <p>
 * The item system provides abstractions for custom items that integrate with
 * Minecraft's inventory system while maintaining Foundry's type safety.
 *
 * <h2>Design Principles</h2>
 *
 * <p>
 * Unlike blocks, Foundry items are implemented directly as ItemStacks with:
 * <ul>
 *   <li>Vanilla Minecraft materials as base types</li>
 *   <li>Custom NBT data for Foundry identification</li>
 *   <li>Custom model data for resource pack support</li>
 *   <li>Persistent data container for additional metadata</li>
 * </ul>
 *
 * <h2>Core Components</h2>
 *
 * <h3>ItemDefinition</h3>
 * <p>
 * Static, type-level properties:
 * <ul>
 *   <li>ID and display name</li>
 *   <li>Base Minecraft material</li>
 *   <li>Lore (description)</li>
 *   <li>Custom model data</li>
 *   <li>Maximum stack size</li>
 *   <li>Optional block association</li>
 * </ul>
 *
 * <h3>BlockItem</h3>
 * <p>
 * Special item type that places blocks:
 * <ul>
 *   <li>References a block definition</li>
 *   <li>Triggers block placement on use</li>
 *   <li>Transfers item NBT to placed block</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Creating a Simple Item</h3>
 * <pre>{@code
 * public class CopperIngot extends AbstractItem {
 *
 *     public CopperIngot() {
 *         super(
 *             new NamespacedKey("example", "copper_ingot"),
 *             Material.IRON_INGOT,
 *             "§6Copper Ingot",
 *             "§7A common industrial metal"
 *         );
 *     }
 *
 *     @Override
 *     public int getCustomModelData() {
 *         return 100001;  // Resource pack model ID
 *     }
 * }
 * }</pre>
 *
 * <h3>Creating a Block Item</h3>
 * <pre>{@code
 * public class GeneratorItem extends BlockItem {
 *
 *     public GeneratorItem() {
 *         super(
 *             new NamespacedKey("example", "generator"),
 *             Material.NOTE_BLOCK,
 *             "§eElectric Generator",
 *             new NamespacedKey("example", "generator_block"),
 *             "§7Generates electricity from fuel",
 *             "§7Right-click to place"
 *         );
 *     }
 * }
 * }</pre>
 *
 * <h3>Registering Items</h3>
 * <pre>{@code
 * @Override
 * public void onEnable() {
 *     MutableRegistry<ItemDefinition> registry =
 *         FoundryAPI.getRegistryManager()
 *                   .getMutableRegistry(FoundryRegistries.ITEMS);
 *
 *     registry.register(
 *         new NamespacedKey(this, "copper_ingot"),
 *         new CopperIngot()
 *     );
 *
 *     registry.register(
 *         new NamespacedKey(this, "generator"),
 *         new GeneratorItem()
 *     );
 * }
 * }</pre>
 *
 * <h3>Creating ItemStacks</h3>
 * <pre>{@code
 * // Get item definition
 * Registry<ItemDefinition> registry =
 *     FoundryAPI.getRegistryManager()
 *               .getRegistry(FoundryRegistries.ITEMS);
 *
 * Optional<ItemDefinition> itemDef = registry.get(
 *     new NamespacedKey("example", "copper_ingot")
 * );
 *
 * // Create item stack
 * if (itemDef.isPresent()) {
 *     ItemStack stack = itemDef.get().createItemStack(16);
 *     player.getInventory().addItem(stack);
 * }
 * }</pre>
 *
 * <h3>Identifying Foundry Items</h3>
 * <pre>{@code
 * @EventHandler
 * public void onInteract(PlayerInteractEvent event) {
 *     ItemStack item = event.getItem();
 *     if (item == null) return;
 *
 *     // Extract Foundry ID from NBT
 *     NamespacedKey foundryId = AbstractItem.getFoundryId(item);
 *     if (foundryId == null) {
 *         // Not a Foundry item
 *         return;
 *     }
 *
 *     // Look up the item definition
 *     Registry<ItemDefinition> registry =
 *         FoundryAPI.getRegistryManager()
 *                   .getRegistry(FoundryRegistries.ITEMS);
 *
 *     Optional<ItemDefinition> itemDef = registry.get(foundryId);
 *     if (itemDef.isEmpty()) {
 *         // Definition not found (removed plugin?)
 *         return;
 *     }
 *
 *     // Handle the interaction
 *     getLogger().info("Player used: " + itemDef.get().getDisplayName());
 * }
 * }</pre>
 *
 * <h2>Item Identification</h2>
 *
 * <p>
 * Foundry items are identified using persistent data:
 * <pre>{@code
 * // Storage key
 * NamespacedKey ITEM_ID_KEY = new NamespacedKey("foundry", "item_id");
 *
 * // Stored value
 * "example:copper_ingot"
 * }</pre>
 *
 * <p>
 * This allows:
 * <ul>
 *   <li>Items to survive inventory operations</li>
 *   <li>Items to be traded and stored</li>
 *   <li>Items to be stacked normally</li>
 *   <li>Fast identification without scanning all definitions</li>
 * </ul>
 *
 * <h2>Custom Model Data</h2>
 *
 * <p>
 * Use custom model data with resource packs for custom appearances:
 * <pre>
 * assets/example/models/item/iron_ingot.json:
 * {
 *   "parent": "item/generated",
 *   "textures": {
 *     "layer0": "item/iron_ingot"
 *   },
 *   "overrides": [
 *     {
 *       "predicate": { "custom_model_data": 100001 },
 *       "model": "example:item/copper_ingot"
 *     }
 *   ]
 * }
 * </pre>
 *
 * <h2>Best Practices</h2>
 * <ol>
 *   <li><b>Immutable Definitions:</b> Item definitions should be immutable</li>
 *   <li><b>Unique IDs:</b> Always use your plugin namespace for item IDs</li>
 *   <li><b>Consistent Materials:</b> Use the same base material for similar items</li>
 *   <li><b>Descriptive Lore:</b> Explain what the item does in the lore</li>
 *   <li><b>Model Data Range:</b> Reserve a range of custom model data IDs</li>
 *   <li><b>Stack Size:</b> Consider gameplay balance when setting max stack size</li>
 *   <li><b>NBT Validation:</b> Always validate NBT data when reading items</li>
 * </ol>
 *
 * @see cn.epicmc.foundry.api.item.ItemDefinition
 * @see cn.epicmc.foundry.api.item.AbstractItem
 * @see cn.epicmc.foundry.api.item.BlockItem
 */
package cn.epicmc.foundry.api.item;
