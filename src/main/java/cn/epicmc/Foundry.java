package cn.epicmc;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Foundry - Minecraft Technology Framework
 * Main plugin entry point
 */
public class Foundry extends JavaPlugin {

    private static Foundry instance;

    @Override
    public void onLoad() {
        instance = this;
        getLogger().info("Foundry is loading...");
    }

    @Override
    public void onEnable() {
        getLogger().info("Foundry has been enabled!");
        getLogger().info("Version: " + getDescription().getVersion());

        // TODO: Initialize registry system
        // TODO: Initialize capability system
        // TODO: Initialize service modules
        // TODO: Load platform adapter
    }

    @Override
    public void onDisable() {
        getLogger().info("Foundry is shutting down...");

        // TODO: Save all machine states
        // TODO: Unload all machines
        // TODO: Close database connections
        // TODO: Cleanup resources
    }

    public static Foundry getInstance() {
        return instance;
    }
}
