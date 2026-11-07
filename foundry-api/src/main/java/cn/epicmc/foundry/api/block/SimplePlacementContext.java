package cn.epicmc.foundry.api.block;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Simple implementation of {@link PlacementContext}.
 */
public class SimplePlacementContext implements PlacementContext {

    private final Player player;
    private final BlockFace clickedFace;
    private final ItemStack itemStack;

    public SimplePlacementContext(Player player, BlockFace clickedFace) {
        this(player, clickedFace, null);
    }

    protected SimplePlacementContext(
        Player player,
        BlockFace clickedFace,
        ItemStack itemStack
    ) {
        this.player = player;
        this.clickedFace = clickedFace;
        this.itemStack = itemStack;
    }

    @Override
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(player);
    }

    @Override
    public BlockFace getClickedFace() {
        return clickedFace;
    }

    @Override
    public BlockFace getPlacementFace() {
        return clickedFace.getOppositeFace();
    }

    @Override
    public Optional<ItemStack> getItemStack() {
        return Optional.ofNullable(itemStack);
    }

    @Override
    public boolean isReplacing() {
        return false;
    }
}
