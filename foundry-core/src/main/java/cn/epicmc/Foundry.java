package cn.epicmc;

import cn.epicmc.foundry.api.FoundryAPI;
import cn.epicmc.foundry.api.registry.RegistryManager;
import cn.epicmc.foundry.core.annotation.AnnotationProcessor;
import cn.epicmc.foundry.core.annotation.AnnotationScanner;
import cn.epicmc.foundry.core.block.BlockManager;
import cn.epicmc.foundry.core.registry.SimpleRegistryManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

/**
 * Main class for the Foundry framework.
 * <p>
 * This plugin provides the core infrastructure for technology mods
 * on Bukkit/Paper servers.
 */
public class Foundry extends JavaPlugin {

    private RegistryManager registryManager;
    private BlockManager blockManager;
    private AnnotationScanner annotationScanner;
    private AnnotationProcessor annotationProcessor;

    @Override
    public void onLoad() {
        getLogger().info("Loading Foundry framework...");

        // Initialize registry system
        registryManager = new SimpleRegistryManager(this);

        // Initialize API
        FoundryAPI.initialize(registryManager);

        getLogger().info("Foundry framework loaded");
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabling Foundry framework...");

        // Initialize managers
        blockManager = new BlockManager();
        annotationScanner = new AnnotationScanner(getLogger());
        annotationProcessor = new AnnotationProcessor(registryManager, getLogger());

        // Register built-in content
        registerBuiltinContent();

        // Scan for annotated classes
        scanAndRegisterAnnotations();

        getLogger().info("Foundry framework enabled");
        getLogger().info("Registered " + blockManager.getBlockCount() + " blocks");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling Foundry framework...");

        // Cleanup
        if (blockManager != null) {
            blockManager.clear();
        }

        // Shutdown API
        FoundryAPI.shutdown();

        getLogger().info("Foundry framework disabled");
    }

    /**
     * Register built-in Foundry content (units, etc).
     */
    private void registerBuiltinContent() {
        // This will be implemented when we add the standard units registration
        getLogger().info("Registering built-in content...");

        // TODO: Register standard units
        // StandardUnits.register(registryManager);
    }

    /**
     * Scan plugins for @Foundry annotations and register them.
     */
    private void scanAndRegisterAnnotations() {
        getLogger().info("Scanning for @Foundry annotations...");

        // Scan this plugin
        Set<Class<?>> foundryClasses = annotationScanner.scanPlugin(this);

        // Process annotations
        if (!foundryClasses.isEmpty()) {
            annotationProcessor.processClasses(foundryClasses);
        }

        // TODO: Scan other plugins that depend on Foundry
    }

    /**
     * Get the block manager.
     *
     * @return The block manager
     */
    public BlockManager getBlockManager() {
        return blockManager;
    }
}
