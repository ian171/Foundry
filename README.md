# Foundry

> A modern technology framework for Minecraft servers, providing modular systems for energy, fluids, machines, and automation.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Paper](https://img.shields.io/badge/Paper-1.21-green.svg)](https://papermc.io/)

## Overview

Foundry is a comprehensive technology framework designed for Minecraft plugin developers. It provides a capability-based architecture for creating industrial-style content with energy systems, fluid handling, complex machines, and automation networks.

## Core Philosophy

- **Capability-Driven**: Machines declare what they can do, not what they are. Query capabilities at runtime without hard dependencies.
- **Type-Safe**: Strong typing throughout the API prevents runtime errors and enables IDE assistance.
- **Modular Design**: Use only the modules you need. Each system is independent yet interoperable.
- **Performance-First**: Efficient tick handling, lazy evaluation, and async-ready architecture.
- **Developer-Friendly**: Annotation-based registration, sensible defaults, and comprehensive documentation.

## Architecture

### Module Overview

#### **foundry-api**
Public API surface. Defines core interfaces, annotations, and contracts that all other modules implement.

- Registry system with lifecycle management
- Capability framework for extensible behaviors
- Physical unit system (energy, fluids, temperature, pressure)
- Type-safe identifiers and keys

#### **foundry-core**
Framework implementation and runtime engine.

- Registry lifecycle orchestration
- Annotation processor for auto-registration
- Event system and hook points
- Plugin integration layer

#### **foundry-common**
Shared utilities and data structures used across modules.

- Common data types and constants
- Serialization/deserialization utilities
- Math and geometry helpers
- Logging and debugging tools

#### **foundry-block**
Custom block system with state management.

- Block definition and registration
- Block state persistence
- Block entity management
- Custom block interactions

#### **foundry-energy**
Energy generation, storage, and distribution.

- Energy handlers and storage implementations
- Unit conversion (EU, RF, J, FE)
- Energy network routing
- Power generation and consumption APIs

#### **foundry-fluid**
Fluid handling and tank systems.

- Fluid handlers and tank implementations
- Fluid stack management
- Pipe networks and flow simulation
- Temperature-aware fluid properties

#### **foundry-machine**
Tick-based machine infrastructure.

- Machine lifecycle management
- Tick scheduling and optimization
- Progress tracking and state machines
- Multi-block structure support

#### **foundry-gui**
GUI system for machine interfaces.

- Declarative GUI builder
- Slot management and validation
- Progress bars and status displays
- Client-server synchronization

#### **foundry-recipe**
Recipe system for processing machines.

- Recipe definition and matching
- Recipe registry and lookup
- Shapeless and shaped patterns
- Custom recipe types

#### **foundry-storage**
Item and inventory management.

- Item handler implementations
- Slot-based inventory abstractions
- Directional item routing
- Filter and sorting systems

#### **foundry-network**
Network communication layer.

- Packet system for client-server sync
- Capability network propagation
- Chunk-aware network segmentation
- Bandwidth optimization

#### **foundry-entity**
Custom entity support for machines.

- Entity-based machines (minecarts, drones)
- Mobile capability providers
- Entity persistence
- Pathfinding integration

#### **foundry-folia**
Folia (multi-threaded server) compatibility layer.

- Region-aware scheduling
- Thread-safe capability access
- Entity/block region checks
- Fallback for non-Folia servers

#### **foundry-platform**
Platform abstraction for Bukkit/Paper/Spigot variants.

- Platform detection and adaptation
- Version-specific NMS bridges
- Fallback implementations
- Feature flag system

## Quick Start

### Installation

Add Foundry to your plugin:

**Gradle (Kotlin DSL)**
```kotlin
repositories {
    maven("https://maven.pkg.github.com/ian171/Foundry") {
        credentials {
            username = project.findProperty("gpr.user") as String?
            password = project.findProperty("gpr.token") as String?
        }
    }
}

dependencies {
    compileOnly("io.github.ian171:foundry:0.0.1.2-SNAPSHOT")
}
```

**plugin.yml**
```yaml
depend: [Foundry]
```

### Example: Creating a Generator

```java
@Foundry(namespace = "myplugin", id = "coal_generator")
@FoundryBlock(material = Material.NOTE_BLOCK, isMachine = true)
@FoundryMachine(tickInterval = 20)
public class CoalGenerator implements CapabilityProvider {
    
    private final EnergyStorage energy = new SimpleEnergyStorage(10000);
    
    public void tick() {
        if (hasFuel()) {
            energy.insert(100.0, false); // Generate 100 EU/second
        }
    }
    
    @Override
    public <T extends Capability> Optional<T> getCapability(
        CapabilityKey<T> key, Direction side
    ) {
        if (key == FoundryCapabilities.ENERGY) {
            return Optional.of(key.getType().cast(energy));
        }
        return Optional.empty();
    }
    
    @Override
    public Set<CapabilityKey<?>> getCapabilities() {
        return Set.of(FoundryCapabilities.ENERGY);
    }
}
```

## Documentation

- [API Guide](docs/API_GUIDE.md) - Complete API documentation
- [Examples](examples/) - Working example projects
- [Registry System](docs/REGISTRY_SYSTEM.md) - Understanding the registry
- [Development Plan](docs/PLAN.md) - Roadmap and architecture

## Building

```bash
./gradlew build
```

Output: `build/libs/Foundry-<version>.jar`

## Requirements

- **Java 21+**
- **Paper 1.21+** (Spigot/Bukkit compatible)
- **Gradle 8.5+** (for development)

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Follow existing code style
4. Add tests for new functionality
5. Submit a pull request

## License

Foundry is licensed under the [GNU General Public License v3.0](LICENSE).

This allows you to:
- Use Foundry in your plugins (including commercial ones)
- Modify and distribute modified versions
- Must disclose source code of modifications
- Must use GPL v3 for derivative works

## Support

- **Issues**: [GitHub Issues](https://github.com/ian171/Foundry/issues)
- **Discussions**: [GitHub Discussions](https://github.com/ian171/Foundry/discussions)

---

**Foundry** - Build industrial automation in Minecraft, the right way.
