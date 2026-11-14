package cn.epicmc.foundry.core.event;

import cn.epicmc.foundry.api.block.BlockDefinition;
import cn.epicmc.foundry.api.block.BlockInstance;
import cn.epicmc.foundry.api.block.PlacementContext;
import cn.epicmc.foundry.api.registry.FoundryRegistries;
import cn.epicmc.foundry.api.registry.Registry;
import cn.epicmc.foundry.api.registry.RegistryManager;
import cn.epicmc.foundry.core.block.BlockManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Bukkit event listener for Foundry blocks.
 * <p>
 * This listener intercepts Bukkit block events and dispatches them to
 * the appropriate Foundry block definitions via {@link BlockEventDispatcher}.
 */
public class FoundryBlockListener implements Listener {

    private final BlockManager blockManager;
    private final RegistryManager registryManager;
    private final BlockEventDispatcher eventDispatcher;
    private final Logger logger;

    public FoundryBlockListener(
        BlockManager blockManager,
        RegistryManager registryManager,
        BlockEventDispatcher eventDispatcher,
        Logger logger
    ) {
        this.blockManager = blockManager;
        this.registryManager = registryManager;
        this.eventDispatcher = eventDispatcher;
        this.logger = logger;
    }

    /**
     * Handle block placement events.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        // Check if this is a Foundry block
        Optional<BlockInstance> instanceOpt = blockManager.getBlock(event.getBlock().getLocation());

        if (instanceOpt.isPresent()) {
            BlockInstance instance = instanceOpt.get();

            // Get block definition from registry
            Registry<BlockDefinition> registry =
                registryManager.getRegistry(FoundryRegistries.BLOCKS);

            Optional<BlockDefinition> definitionOpt = registry.get(instance.getDefinitionId());

            if (definitionOpt.isPresent()) {
                BlockDefinition definition = definitionOpt.get();

                // Create placement context
                PlacementContext context = createPlacementContext(event);

                // Dispatch event
                eventDispatcher.dispatchPlace(definition, context);

                logger.fine("Placed Foundry block: " + instance.getDefinitionId());
            }
        }
    }

    /**
     * Handle block break events.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Optional<BlockInstance> instanceOpt = blockManager.getBlock(location);

        if (instanceOpt.isPresent()) {
            BlockInstance instance = instanceOpt.get();

            // Get block definition from registry
            Registry<BlockDefinition> registry =
                registryManager.getRegistry(FoundryRegistries.BLOCKS);

            Optional<BlockDefinition> definitionOpt = registry.get(instance.getDefinitionId());

            if (definitionOpt.isPresent()) {
                BlockDefinition definition = definitionOpt.get();

                // Dispatch event
                boolean cancel = eventDispatcher.dispatchBreak(definition, event.getPlayer());

                if (cancel) {
                    event.setCancelled(true);
                    logger.fine("Cancelled break of Foundry block: " + instance.getDefinitionId());
                } else {
                    // Unregister block
                    blockManager.unregisterBlock(instance);
                    logger.fine("Broke Foundry block: " + instance.getDefinitionId());
                }
            }
        }
    }

    /**
     * Handle player interaction events.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Optional<BlockInstance> instanceOpt = blockManager.getBlock(block.getLocation());

        if (instanceOpt.isPresent()) {
            BlockInstance instance = instanceOpt.get();

            // Get block definition from registry
            Registry<BlockDefinition> registry =
                registryManager.getRegistry(FoundryRegistries.BLOCKS);

            Optional<BlockDefinition> definitionOpt = registry.get(instance.getDefinitionId());

            if (definitionOpt.isPresent()) {
                BlockDefinition definition = definitionOpt.get();

                // Dispatch event
                boolean cancel = eventDispatcher.dispatchInteract(
                    definition,
                    event.getPlayer(),
                    event.getAction()
                );

                if (cancel) {
                    event.setCancelled(true);
                    logger.fine("Handled interaction with Foundry block: " +
                              instance.getDefinitionId());
                }
            }
        }
    }

    /**
     * Handle block explosion events.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockExplode(BlockExplodeEvent event) {
        handleExplosion(event.blockList().iterator(), event.getYield());
    }

    /**
     * Handle entity explosion events.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        handleExplosion(event.blockList().iterator(), event.getYield());
    }

    /**
     * Handle explosion damage to Foundry blocks.
     */
    private void handleExplosion(Iterator<Block> blocks, float yield) {
        Registry<BlockDefinition> registry =
            registryManager.getRegistry(FoundryRegistries.BLOCKS);

        while (blocks.hasNext()) {
            Block block = blocks.next();
            Optional<BlockInstance> instanceOpt = blockManager.getBlock(block.getLocation());

            if (instanceOpt.isPresent()) {
                BlockInstance instance = instanceOpt.get();
                Optional<BlockDefinition> definitionOpt = registry.get(instance.getDefinitionId());

                if (definitionOpt.isPresent()) {
                    BlockDefinition definition = definitionOpt.get();

                    // Dispatch event
                    boolean allowDestruction = eventDispatcher.dispatchExplode(definition, yield);

                    if (!allowDestruction) {
                        // Remove from explosion block list
                        blocks.remove();
                        logger.fine("Protected Foundry block from explosion: " +
                                  instance.getDefinitionId());
                    } else {
                        // Unregister block
                        blockManager.unregisterBlock(instance);
                        logger.fine("Foundry block destroyed by explosion: " +
                                  instance.getDefinitionId());
                    }
                }
            }
        }
    }

    /**
     * Create a placement context from a block place event.
     */
    private PlacementContext createPlacementContext(BlockPlaceEvent event) {
        return new PlacementContext() {
            @Override
            public Optional<Player> getPlayer() {
                return Optional.of(event.getPlayer());
            }

            /**
             * Get the face of the block that was clicked during placement.
             * <p>
             * This can be used to determine the orientation of the placed block.
             *
             * @return The clicked block face
             */
            @Override
            public BlockFace getClickedFace() {
                return event.getBlockReplacedState().getBlock().getFace(event.getBlock());
            }

            /**
             * Get the face of the new block relative to the clicked block.
             * <p>
             * This is typically the opposite of the clicked face and represents
             * the direction the block is facing.
             *
             * @return The placement face
             */
            @Override
            public BlockFace getPlacementFace() {
                return event.getBlockPlaced().getFace(event.getBlock());
            }

            /**
             * Get the item stack used to place this block.
             * <p>
             * This can contain custom NBT data or other information that should
             * be transferred to the placed block.
             *
             * @return The item stack, or empty if not placed from an item
             */
            @Override
            public Optional<ItemStack> getItemStack() {
                return Optional.empty();
            }

            /**
             * Check if this placement should replace an existing block.
             * <p>
             * True if the block is being placed into a replaceable block
             * (like tall grass, water, etc).
             *
             * @return {@code true} if replacing, {@code false} otherwise
             */
            @Override
            public boolean isReplacing() {
                return false;
            }
        };
    }
}
