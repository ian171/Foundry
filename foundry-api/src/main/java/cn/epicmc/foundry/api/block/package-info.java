/**
 * Block system for the Foundry framework.
 * <p>
 * The block system provides abstractions for custom blocks that are separate from
 * Minecraft's physical block system. This allows flexibility in implementation and
 * avoids NMS dependencies.
 *
 * <h2>Logical vs Physical Separation</h2>
 *
 * <p>
 * The Foundry block system separates logical blocks from physical blocks:
 * <ul>
 *   <li><b>Logical Block:</b> Machine definition, capabilities, behavior, state</li>
 *   <li><b>Physical Block:</b> Minecraft Material, BlockData, location, visual appearance</li>
 * </ul>
 *
 * <h3>Why Separate?</h3>
 * <ul>
 *   <li>Avoid NMS dependencies - no custom block registration required</li>
 *   <li>Cross-version compatibility - works on all Bukkit/Paper versions</li>
 *   <li>Flexible visuals - can use resource packs, BlockDisplay, or vanilla blocks</li>
 *   <li>Dynamic appearance - visuals can change without affecting the machine</li>
 * </ul>
 *
 * <h2>Core Components</h2>
 *
 * <h3>BlockDefinition</h3>
 * <p>
 * Static, type-level properties:
 * <ul>
 *   <li>ID and display name</li>
 *   <li>Base Minecraft material</li>
 *   <li>Hardness and blast resistance</li>
 *   <li>Whether it's a machine</li>
 *   <li>Factory for creating instances</li>
 * </ul>
 *
 * <h3>BlockInstance</h3>
 * <p>
 * Runtime representation of a placed block:
 * <ul>
 *   <li>Unique UUID</li>
 *   <li>World location</li>
 *   <li>Current state (IDLE, ACTIVE, etc)</li>
 *   <li>Validation status</li>
 * </ul>
 *
 * <h3>BlockBinding</h3>
 * <p>
 * Links physical blocks to logical instances:
 * <ul>
 *   <li>Maps world coordinates to machine UUIDs</li>
 *   <li>Allows fast lookup when blocks are interacted with</li>
 *   <li>Handles cleanup when blocks are removed</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Creating a Custom Block</h3>
 * <pre>{@code
 * public class GeneratorBlock extends AbstractBlock {
 *
 *     public GeneratorBlock() {
 *         super(
 *             new NamespacedKey("example", "generator"),
 *             Material.NOTE_BLOCK,        // Use NOTE_BLOCK for custom models
 *             "Electric Generator"
 *         );
 *     }
 *
 *     @Override
 *     public boolean isMachine() {
 *         return true;
 *     }
 *
 *     @Override
 *     public BlockFactory getFactory() {
 *         return (def, loc, ctx) -> {
 *             // Create the machine instance
 *             GeneratorMachine machine = new GeneratorMachine(loc);
 *
 *             // Apply placement context (rotation, etc)
 *             ctx.getPlayer().ifPresent(player -> {
 *                 machine.setOwner(player.getUniqueId());
 *             });
 *
 *             return machine;
 *         };
 *     }
 * }
 * }</pre>
 *
 * <h3>Registering the Block</h3>
 * <pre>{@code
 * @Override
 * public void onEnable() {
 *     MutableRegistry<BlockDefinition> registry =
 *         FoundryAPI.getRegistryManager()
 *                   .getMutableRegistry(FoundryRegistries.BLOCKS);
 *
 *     BlockDefinition generator = new GeneratorBlock();
 *     registry.register(generator.getId(), generator);
 * }
 * }</pre>
 *
 * <h3>Handling Block Placement</h3>
 * <pre>{@code
 * @EventHandler
 * public void onBlockPlace(BlockPlaceEvent event) {
 *     ItemStack item = event.getItemInHand();
 *
 *     // Check if it's a Foundry block item
 *     NamespacedKey itemId = AbstractItem.getFoundryId(item);
 *     if (itemId == null) return;
 *
 *     // Get the item definition
 *     Registry<ItemDefinition> items =
 *         FoundryAPI.getRegistryManager()
 *                   .getRegistry(FoundryRegistries.ITEMS);
 *
 *     Optional<ItemDefinition> itemDef = items.get(itemId);
 *     if (itemDef.isEmpty() || !itemDef.get().isBlockItem()) return;
 *
 *     // Get the block definition
 *     NamespacedKey blockId = itemDef.get().getBlockDefinition();
 *     Registry<BlockDefinition> blocks =
 *         FoundryAPI.getRegistryManager()
 *                   .getRegistry(FoundryRegistries.BLOCKS);
 *
 *     Optional<BlockDefinition> blockDef = blocks.get(blockId);
 *     if (blockDef.isEmpty()) return;
 *
 *     // Create placement context
 *     PlacementContext context = new SimplePlacementContext(
 *         event.getPlayer(),
 *         event.getBlockAgainst().getFace(event.getBlock()),
 *         item
 *     );
 *
 *     // Create and register the instance
 *     BlockFactory factory = blockDef.get().getFactory();
 *     BlockInstance instance = factory.create(
 *         blockDef.get(),
 *         event.getBlock().getLocation(),
 *         context
 *     );
 *
 *     // Register with block manager
 *     FoundryAPI.getBlockManager().registerBlock(instance);
 * }
 * }</pre>
 *
 * <h2>Visual Implementation Strategies</h2>
 *
 * <h3>Strategy 1: Vanilla Blocks</h3>
 * <p>
 * Use vanilla Minecraft blocks as-is:
 * <ul>
 *   <li>Pros: Simple, no resource pack needed</li>
 *   <li>Cons: Limited visual variety</li>
 *   <li>Example: Use FURNACE for all machines</li>
 * </ul>
 *
 * <h3>Strategy 2: Custom Model Data</h3>
 * <p>
 * Use NOTE_BLOCK with custom model data:
 * <ul>
 *   <li>Pros: Full custom models via resource pack</li>
 *   <li>Cons: Requires resource pack download</li>
 *   <li>Best for: Professional servers with custom content</li>
 * </ul>
 *
 * <h3>Strategy 3: BlockDisplay Entities</h3>
 * <p>
 * Use invisible BARRIER with BlockDisplay entity overlay:
 * <ul>
 *   <li>Pros: Dynamic visuals, no resource pack</li>
 *   <li>Cons: Entity performance overhead</li>
 *   <li>Best for: Complex multi-block structures</li>
 * </ul>
 *
 * <h3>Strategy 4: Player Heads</h3>
 * <p>
 * Use PLAYER_HEAD with custom textures:
 * <ul>
 *   <li>Pros: Custom textures, no resource pack</li>
 *   <li>Cons: Cannot rotate, single texture per block</li>
 *   <li>Best for: Simple decorative blocks</li>
 * </ul>
 *
 * <h2>Best Practices</h2>
 * <ol>
 *   <li><b>Immutable Definitions:</b> Block definitions should be immutable</li>
 *   <li><b>Separate State:</b> Keep mutable state in instances, not definitions</li>
 *   <li><b>Validate on Break:</b> Always clean up instances when blocks are broken</li>
 *   <li><b>Handle WorldEdit:</b> Detect and handle bulk block changes</li>
 *   <li><b>Chunk Unload:</b> Properly save state when chunks unload</li>
 *   <li><b>Thread Safety:</b> Block instances may be accessed from multiple threads</li>
 * </ol>
 *
 * @see cn.epicmc.foundry.api.block.BlockDefinition
 * @see cn.epicmc.foundry.api.block.BlockInstance
 * @see cn.epicmc.foundry.api.block.AbstractBlock
 */
package cn.epicmc.foundry.api.block;
