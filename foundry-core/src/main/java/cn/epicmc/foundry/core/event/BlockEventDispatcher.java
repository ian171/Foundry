package cn.epicmc.foundry.core.event;

import cn.epicmc.foundry.api.annotations.event.*;
import cn.epicmc.foundry.api.block.BlockDefinition;
import cn.epicmc.foundry.api.block.PlacementContext;
import cn.epicmc.foundry.core.block.BlockManager;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

/**
 * Dispatches events to @Foundry annotated block methods.
 * <p>
 * This class handles the invocation of event handler methods annotated with:
 * <ul>
 *   <li>{@link OnPlace} - Block placement</li>
 *   <li>{@link OnBreak} - Block breaking</li>
 *   <li>{@link OnInteract} - Player interaction</li>
 *   <li>{@link OnExplode} - Explosion damage</li>
 * </ul>
 * <p>
 * Event handlers are discovered via reflection and cached for performance.
 */
public class BlockEventDispatcher {

    private final BlockManager blockManager;
    private final Logger logger;

    // Cache for event handlers
    private final Map<Class<?>, EventHandlers> handlerCache = new HashMap<>();

    public BlockEventDispatcher(BlockManager blockManager, Logger logger) {
        this.blockManager = Objects.requireNonNull(blockManager, "blockManager");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    /**
     * Dispatch a place event to the block definition.
     *
     * @param definition The block definition
     * @param context The placement context
     */
    public void dispatchPlace(BlockDefinition definition, PlacementContext context) {
        EventHandlers handlers = getHandlers(definition.getClass());

        if (handlers.onPlace != null) {
            try {
                handlers.onPlace.invoke(definition, context);
            } catch (Exception e) {
                logger.warning("Error dispatching OnPlace event for " +
                             definition.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Dispatch a break event to the block definition.
     *
     * @param definition The block definition
     * @param player The player breaking the block
     * @return true to cancel the break event
     */
    public boolean dispatchBreak(BlockDefinition definition, Player player) {
        EventHandlers handlers = getHandlers(definition.getClass());

        if (handlers.onBreak != null) {
            try {
                Object result = handlers.onBreak.invoke(definition, player);

                // Check return type
                if (result instanceof Boolean) {
                    return (Boolean) result;
                }
                // void methods don't cancel
                return false;

            } catch (Exception e) {
                logger.warning("Error dispatching OnBreak event for " +
                             definition.getId() + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    /**
     * Dispatch an interact event to the block definition.
     *
     * @param definition The block definition
     * @param player The player interacting
     * @param action The interaction action
     * @return true to cancel the interaction event
     */
    public boolean dispatchInteract(BlockDefinition definition, Player player, Action action) {
        EventHandlers handlers = getHandlers(definition.getClass());

        if (handlers.onInteract != null) {
            try {
                Object result = handlers.onInteract.invoke(definition, player, action);

                // Check return type
                if (result instanceof Boolean) {
                    return (Boolean) result;
                }
                return false;

            } catch (Exception e) {
                logger.warning("Error dispatching OnInteract event for " +
                             definition.getId() + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    /**
     * Dispatch an explode event to the block definition.
     *
     * @param definition The block definition
     * @param yield The explosion yield
     * @return true to allow the block to be destroyed
     */
    public boolean dispatchExplode(BlockDefinition definition, float yield) {
        EventHandlers handlers = getHandlers(definition.getClass());

        if (handlers.onExplode != null) {
            try {
                Object result = handlers.onExplode.invoke(definition, yield);

                // Check return type
                if (result instanceof Boolean) {
                    return (Boolean) result;
                }
                return true; // Default: allow destruction

            } catch (Exception e) {
                logger.warning("Error dispatching OnExplode event for " +
                             definition.getId() + ": " + e.getMessage());
                e.printStackTrace();
                return true;
            }
        }

        return true;
    }

    /**
     * Get cached event handlers for a block definition class.
     */
    private EventHandlers getHandlers(Class<?> clazz) {
        return handlerCache.computeIfAbsent(clazz, this::scanHandlers);
    }

    /**
     * Scan a class for event handler methods.
     */
    private EventHandlers scanHandlers(Class<?> clazz) {
        EventHandlers handlers = new EventHandlers();

        // Scan all declared methods
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);

            if (method.isAnnotationPresent(OnPlace.class)) {
                validatePlaceHandler(method, clazz);
                handlers.onPlace = method;
            }

            if (method.isAnnotationPresent(OnBreak.class)) {
                validateBreakHandler(method, clazz);
                handlers.onBreak = method;
            }

            if (method.isAnnotationPresent(OnInteract.class)) {
                validateInteractHandler(method, clazz);
                handlers.onInteract = method;
            }

            if (method.isAnnotationPresent(OnExplode.class)) {
                validateExplodeHandler(method, clazz);
                handlers.onExplode = method;
            }
        }

        return handlers;
    }

    /**
     * Validate @OnPlace handler signature.
     */
    private void validatePlaceHandler(Method method, Class<?> clazz) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1 || !PlacementContext.class.isAssignableFrom(params[0])) {
            logger.warning("Invalid @OnPlace signature in " + clazz.getName() +
                         "." + method.getName() +
                         " - expected: void onPlace(PlacementContext)");
        }
    }

    /**
     * Validate @OnBreak handler signature.
     */
    private void validateBreakHandler(Method method, Class<?> clazz) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1 || !Player.class.isAssignableFrom(params[0])) {
            logger.warning("Invalid @OnBreak signature in " + clazz.getName() +
                         "." + method.getName() +
                         " - expected: boolean onBreak(Player)");
        }
    }

    /**
     * Validate @OnInteract handler signature.
     */
    private void validateInteractHandler(Method method, Class<?> clazz) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 2 ||
            !Player.class.isAssignableFrom(params[0]) ||
            !Action.class.isAssignableFrom(params[1])) {
            logger.warning("Invalid @OnInteract signature in " + clazz.getName() +
                         "." + method.getName() +
                         " - expected: boolean onInteract(Player, Action)");
        }
    }

    /**
     * Validate @OnExplode handler signature.
     */
    private void validateExplodeHandler(Method method, Class<?> clazz) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1 || params[0] != float.class) {
            logger.warning("Invalid @OnExplode signature in " + clazz.getName() +
                         "." + method.getName() +
                         " - expected: boolean onExplode(float)");
        }
    }

    /**
     * Holder for cached event handler methods.
     */
    private static class EventHandlers {
        Method onPlace;
        Method onBreak;
        Method onInteract;
        Method onExplode;
    }
}
