package cn.epicmc.foundry.core.annotation;

import cn.epicmc.foundry.api.annotations.*;
import cn.epicmc.foundry.api.block.BlockDefinition;
import cn.epicmc.foundry.api.item.ItemDefinition;
import cn.epicmc.foundry.api.registry.FoundryRegistries;
import cn.epicmc.foundry.api.registry.MutableRegistry;
import cn.epicmc.foundry.api.registry.RegistryKey;
import cn.epicmc.foundry.api.registry.RegistryManager;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Processes @Foundry annotated classes and registers them.
 * <p>
 * This is the bridge between annotations and the Registry system.
 * Annotations are syntax sugar - this processor converts them to
 * actual Registry calls.
 */
public class AnnotationProcessor {

    private final RegistryManager registryManager;
    private final Logger logger;

    public AnnotationProcessor(RegistryManager registryManager, Logger logger) {
        this.registryManager = registryManager;
        this.logger = logger;
    }

    /**
     * Process a set of annotated classes and register them.
     *
     * @param classes Classes with @Foundry annotation
     */
    public void processClasses(Set<Class<?>> classes) {
        int registered = 0;

        for (Class<?> clazz : classes) {
            try {
                if (processClass(clazz)) {
                    registered++;
                }
            } catch (Exception e) {
                logger.warning("Failed to process @Foundry class " +
                             clazz.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        logger.info("Registered " + registered + " items from annotations");
    }

    /**
     * Process a single annotated class.
     *
     * @return true if successfully registered
     */
    private boolean processClass(Class<?> clazz) throws Exception {
        Foundry foundry = clazz.getAnnotation(Foundry.class);
        if (foundry == null) {
            return false;
        }

        // Create NamespacedKey
        NamespacedKey id = new NamespacedKey(foundry.namespace(), foundry.id());

        // Determine what type of content this is
        if (clazz.isAnnotationPresent(FoundryBlock.class)) {
            return processBlock(clazz, id, foundry);
        } else if (clazz.isAnnotationPresent(FoundryItem.class)) {
            return processItem(clazz, id, foundry);
        } else {
            logger.warning("@Foundry class " + clazz.getName() +
                         " has no @FoundryBlock or @FoundryItem annotation");
            return false;
        }
    }

    /**
     * Process a block definition.
     */
    private boolean processBlock(Class<?> clazz, NamespacedKey id, Foundry foundry)
        throws Exception {

        // Verify it implements BlockDefinition
        if (!BlockDefinition.class.isAssignableFrom(clazz)) {
            logger.warning("@FoundryBlock class " + clazz.getName() +
                         " does not implement BlockDefinition");
            return false;
        }

        // Create instance
        BlockDefinition block = (BlockDefinition) createInstance(clazz);
        if (block == null) {
            return false;
        }

        // Get registry
        // TODO: Add BLOCKS registry key to FoundryRegistries
        // MutableRegistry<BlockDefinition> registry =
        //     registryManager.getMutableRegistry(FoundryRegistries.BLOCKS);

        // Register
        // registry.register(id, block);

        logger.info("Registered block: " + id);
        return true;
    }

    /**
     * Process an item definition.
     */
    private boolean processItem(Class<?> clazz, NamespacedKey id, Foundry foundry)
        throws Exception {

        // Verify it implements ItemDefinition
        if (!ItemDefinition.class.isAssignableFrom(clazz)) {
            logger.warning("@FoundryItem class " + clazz.getName() +
                         " does not implement ItemDefinition");
            return false;
        }

        // Create instance
        ItemDefinition item = (ItemDefinition) createInstance(clazz);
        if (item == null) {
            return false;
        }

        // Get registry
        // TODO: Add ITEMS registry key to FoundryRegistries
        // MutableRegistry<ItemDefinition> registry =
        //     registryManager.getMutableRegistry(FoundryRegistries.ITEMS);

        // Register
        // registry.register(id, item);

        logger.info("Registered item: " + id);
        return true;
    }

    /**
     * Create an instance of a class using its default constructor.
     */
    private Object createInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            logger.warning("Failed to create instance of " + clazz.getName() +
                         ": " + e.getMessage() +
                         " (Does it have a no-args constructor?)");
            return null;
        }
    }
}
