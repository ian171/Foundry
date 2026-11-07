package cn.epicmc.foundry.common.capability;

import cn.epicmc.foundry.api.capability.Capability;
import cn.epicmc.foundry.api.capability.CapabilityKey;
import cn.epicmc.foundry.api.capability.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for SimpleCapabilityProvider.
 */
class SimpleCapabilityProviderTest {

    // Test capability interfaces
    interface EnergyHandler extends Capability {
        double getStored();
    }

    interface FluidHandler extends Capability {
        long getAmount();
    }

    // Test capability keys
    static final CapabilityKey<EnergyHandler> ENERGY =
        CapabilityKey.of("test", "energy", EnergyHandler.class);

    static final CapabilityKey<FluidHandler> FLUID =
        CapabilityKey.of("test", "fluid", FluidHandler.class);

    // Test implementations
    static class SimpleEnergyStorage implements EnergyHandler {
        private final double stored;

        SimpleEnergyStorage(double stored) {
            this.stored = stored;
        }

        @Override
        public double getStored() {
            return stored;
        }
    }

    static class SimpleFluidTank implements FluidHandler {
        private final long amount;

        SimpleFluidTank(long amount) {
            this.amount = amount;
        }

        @Override
        public long getAmount() {
            return amount;
        }
    }

    private SimpleCapabilityProvider provider;

    @BeforeEach
    void setUp() {
        provider = new SimpleCapabilityProvider();
    }

    @Test
    void testRegisterGlobalCapability() {
        EnergyHandler energy = new SimpleEnergyStorage(1000);
        provider.registerCapability(ENERGY, energy);

        // Should be available from all sides
        for (Direction dir : Direction.values()) {
            Optional<EnergyHandler> result = provider.getCapability(ENERGY, dir);
            assertTrue(result.isPresent());
            assertEquals(1000, result.get().getStored());
        }
    }

    @Test
    void testRegisterDirectionalCapability() {
        FluidHandler fluid = new SimpleFluidTank(5000);
        provider.registerCapability(FLUID, fluid, Direction.UP, Direction.DOWN);

        // Should be available from UP and DOWN
        assertTrue(provider.getCapability(FLUID, Direction.UP).isPresent());
        assertTrue(provider.getCapability(FLUID, Direction.DOWN).isPresent());

        // Should NOT be available from other sides
        assertFalse(provider.getCapability(FLUID, Direction.NORTH).isPresent());
        assertFalse(provider.getCapability(FLUID, Direction.SOUTH).isPresent());
        assertFalse(provider.getCapability(FLUID, Direction.EAST).isPresent());
        assertFalse(provider.getCapability(FLUID, Direction.WEST).isPresent());
    }

    @Test
    void testRegisterSidedCapabilities() {
        FluidHandler input = new SimpleFluidTank(1000);
        FluidHandler output = new SimpleFluidTank(2000);

        provider.registerCapability(FLUID, Map.of(
            Direction.UP, input,
            Direction.DOWN, output
        ));

        // UP should return input tank
        Optional<FluidHandler> upResult = provider.getCapability(FLUID, Direction.UP);
        assertTrue(upResult.isPresent());
        assertEquals(1000, upResult.get().getAmount());

        // DOWN should return output tank
        Optional<FluidHandler> downResult = provider.getCapability(FLUID, Direction.DOWN);
        assertTrue(downResult.isPresent());
        assertEquals(2000, downResult.get().getAmount());

        // Other sides should be empty
        assertFalse(provider.getCapability(FLUID, Direction.NORTH).isPresent());
    }

    @Test
    void testUnregisterCapability() {
        EnergyHandler energy = new SimpleEnergyStorage(1000);
        provider.registerCapability(ENERGY, energy);

        assertTrue(provider.hasCapability(ENERGY));

        provider.unregisterCapability(ENERGY);

        assertFalse(provider.hasCapability(ENERGY));
        assertFalse(provider.getCapability(ENERGY, Direction.UP).isPresent());
    }

    @Test
    void testGetCapabilities() {
        EnergyHandler energy = new SimpleEnergyStorage(1000);
        FluidHandler fluid = new SimpleFluidTank(5000);

        provider.registerCapability(ENERGY, energy);
        provider.registerCapability(FLUID, fluid);

        var capabilities = provider.getCapabilities();
        assertEquals(2, capabilities.size());
        assertTrue(capabilities.contains(ENERGY));
        assertTrue(capabilities.contains(FLUID));
    }

    @Test
    void testHasCapability() {
        EnergyHandler energy = new SimpleEnergyStorage(1000);
        provider.registerCapability(ENERGY, energy, Direction.UP);

        // Should be true for any side (checks if capability exists)
        assertTrue(provider.hasCapability(ENERGY));

        // Should be true for UP specifically
        assertTrue(provider.hasCapability(ENERGY, Direction.UP));

        // Should be false for NORTH specifically
        assertFalse(provider.hasCapability(ENERGY, Direction.NORTH));

        // Should be false for non-registered capability
        assertFalse(provider.hasCapability(FLUID));
    }

    @Test
    void testNullChecks() {
        assertThrows(NullPointerException.class, () -> {
            provider.registerCapability(null, new SimpleEnergyStorage(1000));
        });

        assertThrows(NullPointerException.class, () -> {
            provider.registerCapability(ENERGY, null);
        });

        assertThrows(NullPointerException.class, () -> {
            provider.getCapability(null, Direction.UP);
        });

        assertThrows(NullPointerException.class, () -> {
            provider.getCapability(ENERGY, null);
        });
    }

    @Test
    void testEmptyDirectionsArray() {
        assertThrows(IllegalArgumentException.class, () -> {
            provider.registerCapability(ENERGY, new SimpleEnergyStorage(1000));
        });
    }
}
