package cn.epicmc;

import cn.epicmc.foundry.api.FoundryAPI;
import cn.epicmc.foundry.api.registry.RegistryManager;
import cn.epicmc.foundry.common.unit.DependencyFinder;
import cn.epicmc.foundry.common.unit.StandardUnits;
import cn.epicmc.foundry.common.unit.StandardUnitsRegistration;
import cn.epicmc.foundry.core.annotation.AnnotationProcessor;
import cn.epicmc.foundry.core.annotation.AnnotationScanner;
import cn.epicmc.foundry.core.block.BlockManager;
import cn.epicmc.foundry.core.event.BlockEventDispatcher;
import cn.epicmc.foundry.core.event.FoundryBlockListener;
import cn.epicmc.foundry.core.registry.SimpleRegistryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
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
    private BlockEventDispatcher eventDispatcher;
    private FoundryBlockListener blockListener;

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
        printBanner();
        // Initialize managers
        blockManager = new BlockManager();
        annotationScanner = new AnnotationScanner(getLogger());
        annotationProcessor = new AnnotationProcessor(registryManager, getLogger());

        // Initialize event system
        eventDispatcher = new BlockEventDispatcher(blockManager, getLogger());
        blockListener = new FoundryBlockListener(
            blockManager,
            registryManager,
            eventDispatcher,
            getLogger()
        );

        // Register event listener
        getServer().getPluginManager().registerEvents(blockListener, this);

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
        StandardUnitsRegistration.registerAll(registryManager);
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
        List<Plugin> foundryDependency = DependencyFinder.getDependentPlugins("Foundry", true);

        for (Plugin p : foundryDependency) {
            getLogger().info(" - " + p.getName() + " (版本: " + p.getDescription().getVersion() + ")");
        }
    }

    /**
     * Get the block manager.
     *
     * @return The block manager
     */
    public BlockManager getBlockManager() {
        return blockManager;
    }

    /**
     * Get the event dispatcher.
     *
     * @return The event dispatcher
     */
    public BlockEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }


    private void printBanner(){
        this.getLogger().info("""
                ███████╗ ██████╗ ██╗   ██╗███╗   ██╗██████╗ ██████╗ ██╗   ██╗
                ██╔════╝██╔═══██╗██║   ██║████╗  ██║██╔══██╗██╔══██╗╚██╗ ██╔╝
                █████╗  ██║   ██║██║   ██║██╔██╗ ██║██║  ██║██████╔╝ ╚████╔╝\s
                ██╔══╝  ██║   ██║██║   ██║██║╚██╗██║██║  ██║██╔══██╗  ╚██╔╝ \s
                ██║     ╚██████╔╝╚██████╔╝██║ ╚████║██████╔╝██║  ██║   ██║  \s
                ╚═╝      ╚═════╝  ╚═════╝ ╚═╝  ╚═══╝╚═════╝ ╚═╝  ╚═╝   ╚═╝  \s
                
                """);

    }
}
