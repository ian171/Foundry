rootProject.name = "Foundry"

// API Module
include("foundry-api")

// Core Module
include("foundry-core")

// Common Module
include("foundry-common")

// Service Modules
include("foundry-energy")
include("foundry-fluid")
include("foundry-block")
include("foundry-machine")
include("foundry-network")
include("foundry-gui")
include("foundry-entity")
include("foundry-recipe")
include("foundry-storage")

// Platform Modules
include("foundry-platform:platform-bukkit")
include("foundry-platform:platform-paper")

// Folia Module
include("foundry-folia")

// Example Modules
include("examples:example-energy")
include("examples:example-machine")
include("examples:example-industrial")
