package cn.epicmc.foundry.common.events;

import cn.epicmc.foundry.api.block.BlockInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class FoundryEvents {
    public static class CustomBlockCallGenerallyEvent extends Event {
        private final HandlerList handlerList;
        private final Player player;
        private final Location position;
        private final BlockInstance blockInstance;
        public CustomBlockCallGenerallyEvent(@NotNull Player player, @NotNull Location position,@NotNull BlockInstance blockInstance){
            this.handlerList = new HandlerList();
            this.player = player;
            this.position = position;
            this.blockInstance = blockInstance;
        }
        @Override
        public @NotNull HandlerList getHandlers() {
            return handlerList;
        }

        public Player getPlayer() {
            return player;
        }

        public Location getPosition() {
            return position;
        }

        public BlockInstance getBlockInstance() {
            return blockInstance;
        }
    }
}
