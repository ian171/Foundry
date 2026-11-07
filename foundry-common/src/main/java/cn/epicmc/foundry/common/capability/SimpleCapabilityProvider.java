package cn.epicmc.foundry.common.capability;

import cn.epicmc.foundry.api.capability.Capability;
import cn.epicmc.foundry.api.capability.CapabilityKey;
import cn.epicmc.foundry.api.capability.CapabilityProvider;
import cn.epicmc.foundry.api.capability.Direction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple implementation of {@link CapabilityProvider}.
 * <p>
 * Provides a flexible way to implement capability providers by registering
 * capabilities with their handlers.
 * <p>
 * <b>Example Usage:</b>
 * <pre>{@code
 * public class MyMachine extends SimpleCapabilityProvider {
 *
 *     private final EnergyStorage energyStorage = new SimpleEnergyStorage(10000);
 *     private final FluidTank waterTank = new SimpleFluidTank(8000);
 *
 *     public MyMachine() {
 *         // Register energy on all sides
 *         registerCapability(FoundryCapabilities.ENERGY, energyStorage);
 *
 *         // Register fluid only on specific sides
 *         registerCapability(FoundryCapabilities.FLUID, waterTank,
 *                          Direction.UP, Direction.DOWN);
 *     }
 * }
 * }</pre>
 */
public class SimpleCapabilityProvider implements CapabilityProvider {

    private final Map<CapabilityKey<?>, CapabilityHolder<?>> capabilities;

    /**
     * Create a new simple capability provider.
     */
    public SimpleCapabilityProvider() {
        this.capabilities = new ConcurrentHashMap<>();
    }

    /**
     * Register a capability that is available from all sides.
     *
     * @param key The capability key
     * @param handler The capability handler
     * @param <T> The capability type
     */
    protected <T extends Capability> void registerCapability(
        CapabilityKey<T> key,
        T handler
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(handler, "handler");

        capabilities.put(key, new CapabilityHolder<>(handler, true));
    }

    /**
     * Register a capability that is available from specific sides only.
     *
     * @param key The capability key
     * @param handler The capability handler
     * @param sides The sides this capability is available from
     * @param <T> The capability type
     */
    protected <T extends Capability> void registerCapability(
        CapabilityKey<T> key,
        T handler,
        Direction... sides
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(handler, "handler");
        Objects.requireNonNull(sides, "sides");

        if (sides.length == 0) {
            throw new IllegalArgumentException("Must specify at least one side");
        }

        Set<Direction> sideSet = EnumSet.noneOf(Direction.class);
        Collections.addAll(sideSet, sides);

        capabilities.put(key, new CapabilityHolder<>(handler, sideSet));
    }

    /**
     * Register different capability handlers for different sides.
     *
     * @param key The capability key
     * @param handlers Map of direction to handler
     * @param <T> The capability type
     */
    protected <T extends Capability> void registerCapability(
        CapabilityKey<T> key,
        Map<Direction, T> handlers
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(handlers, "handlers");

        if (handlers.isEmpty()) {
            throw new IllegalArgumentException("Must specify at least one handler");
        }

        capabilities.put(key, new CapabilityHolder<>(handlers));
    }

    /**
     * Unregister a capability.
     *
     * @param key The capability key to remove
     */
    protected void unregisterCapability(CapabilityKey<?> key) {
        capabilities.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Capability> Optional<T> getCapability(
        CapabilityKey<T> key,
        Direction side
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(side, "side");

        CapabilityHolder<?> holder = capabilities.get(key);
        if (holder == null) {
            return Optional.empty();
        }

        Object capability = holder.get(side);
        if (capability == null) {
            return Optional.empty();
        }

        // Type safety guaranteed by registerCapability methods
        return Optional.of((T) capability);
    }

    @Override
    public Set<CapabilityKey<?>> getCapabilities() {
        return Collections.unmodifiableSet(capabilities.keySet());
    }

    /**
     * Internal holder for capability instances and their side availability.
     */
    private static class CapabilityHolder<T> {
        private final T globalHandler;
        private final boolean allSides;
        private final Set<Direction> availableSides;
        private final Map<Direction, T> directionalHandlers;

        /**
         * Create a holder for a capability available from all sides.
         */
        CapabilityHolder(T handler, boolean allSides) {
            this.globalHandler = handler;
            this.allSides = allSides;
            this.availableSides = null;
            this.directionalHandlers = null;
        }

        /**
         * Create a holder for a capability available from specific sides.
         */
        CapabilityHolder(T handler, Set<Direction> sides) {
            this.globalHandler = handler;
            this.allSides = false;
            this.availableSides = sides;
            this.directionalHandlers = null;
        }

        /**
         * Create a holder for directional capabilities.
         */
        CapabilityHolder(Map<Direction, T> handlers) {
            this.globalHandler = null;
            this.allSides = false;
            this.availableSides = null;
            this.directionalHandlers = handlers;
        }

        /**
         * Get the capability handler for a specific side.
         */
        T get(Direction side) {
            if (directionalHandlers != null) {
                return directionalHandlers.get(side);
            }

            if (allSides) {
                return globalHandler;
            }

            if (availableSides != null && availableSides.contains(side)) {
                return globalHandler;
            }

            return null;
        }
    }
}
