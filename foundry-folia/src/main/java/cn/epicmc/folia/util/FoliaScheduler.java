package cn.epicmc.folia.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

/**
 * Folia / Paper 兼容调度器工具类
 * 自动检测运行环境，在 Folia 上使用区域化调度器，在 Paper/Spigot 上使用 BukkitScheduler
 */
public final class FoliaScheduler {

    private static final boolean IS_FOLIA;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        IS_FOLIA = folia;
    }

    private FoliaScheduler() {}

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    /**
     * 在全局调度器上运行重复任务（适用于不依赖特定区域的任务，如遍历所有玩家）
     */
    public static TaskHandle runTaskTimerGlobal(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        if (IS_FOLIA) {
            var scheduled = plugin.getServer().getGlobalRegionScheduler()
                    .runAtFixedRate(plugin, t -> task.run(), Math.max(1, delayTicks), periodTicks);
            return scheduled::cancel;
        } else {
            BukkitTask bt = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
            return bt::cancel;
        }
    }

    /**
     * 在区域调度器上运行重复任务（适用于特定位置的任务）
     */
    public static TaskHandle runTaskTimerAtLocation(Plugin plugin, Location location, Runnable task, long delayTicks, long periodTicks) {
        if (IS_FOLIA) {
            ScheduledTask scheduled = plugin.getServer().getRegionScheduler()
                    .runAtFixedRate(plugin, location, t -> task.run(), Math.max(1, delayTicks), periodTicks);
            return scheduled::cancel;
        } else {
            BukkitTask bt = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
            return bt::cancel;
        }
    }

    /**
     * 在玩家所在区域调度器上运行单次任务
     */
    public static void runForPlayer(Plugin plugin, Player player, Runnable task) {
        if (IS_FOLIA) {
            player.getScheduler().run(plugin, i -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * 在全局调度器上运行单次任务
     */
    public static void runTaskGlobal(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            plugin.getServer().getGlobalRegionScheduler().run(plugin, i -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * 在异步线程运行任务（用于 DB / IO 操作，避免阻塞主线程）
     */
    public static void runTaskAsync(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, i -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * 任务句柄，用于取消任务
     */
    @FunctionalInterface
    public interface TaskHandle {
        void cancel();
    }

    public static final class FoliaSchedule {

        public static ScheduledTask runAsyncRepeating(
                Plugin plugin,
                Runnable task,
                long delayTicks,
                long periodTicks
        ) {
            if (IS_FOLIA) {
                // Folia 异步调度
                return Bukkit.getAsyncScheduler().runAtFixedRate(
                        plugin,
                        scheduledTask -> task.run(),
                        delayTicks * 50L,
                        periodTicks * 50L,
                        TimeUnit.MILLISECONDS
                );
            } else {
                // Paper / Spigot 传统异步
                Bukkit.getScheduler().runTaskTimerAsynchronously(
                        plugin,
                        task,
                        delayTicks,
                        periodTicks
                );
                return null;
            }
        }

        public static void runAsyncOnce(Plugin plugin, Runnable task) {
            if (IS_FOLIA) {
                Bukkit.getAsyncScheduler().runNow(
                        plugin,
                        t -> task.run()
                );
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(
                        plugin,
                        task
                );
            }
        }

    }
}
