/**
 * Capacity implementations with NBT persistence support.
 * <p>
 * This package provides concrete implementations of the {@link cn.epicmc.foundry.api.Capacity}
 * interface for different resource types:
 * <ul>
 *   <li>{@link cn.epicmc.foundry.common.capacity.SimpleCapacity} - Basic capacity for energy/fluids</li>
 *   <li>{@link cn.epicmc.foundry.common.capacity.FluidCapacity} - Fluid storage with temperature</li>
 *   <li>{@link cn.epicmc.foundry.common.capacity.ItemCapacity} - Item inventory with slots</li>
 * </ul>
 * <p>
 * <b>Design Pattern:</b>
 * <pre>
 * Capacity (Interface)     NBTSerializable (Interface)
 *         ↓                         ↓
 *         └─────────┬───────────────┘
 *                   ↓
 *           SimpleCapacity
 *                   ↓
 *          ┌────────┴────────┐
 *          ↓                 ↓
 *    FluidCapacity      ItemCapacity
 * </pre>
 * <p>
 * <b>Energy Storage Example:</b>
 * <pre>{@code
 * SimpleCapacity battery = new SimpleCapacity(
 *     EnergyUnit.EU,
 *     10000.0,  // 10k EU
 *     0, 100,   // Temperature not used
 *     true      // Can contain
 * );
 *
 * battery.insert(500.0, false);  // Insert 500 EU
 * battery.save(container);       // Save to NBT
 * }</pre>
 * <p>
 * <b>Fluid Storage Example:</b>
 * <pre>{@code
 * FluidCapacity waterTank = new FluidCapacity(
 *     FluidUnit.MILLIBUCKET,
 *     16000.0,           // 16 buckets
 *     -10.0f, 100.0f,    // -10°C to 100°C
 *     Material.WATER
 * );
 *
 * waterTank.fill(1000.0, false);  // Fill 1 bucket
 * waterTank.setTemperature(25.0f);
 * waterTank.save(container);
 * }</pre>
 * <p>
 * <b>Item Storage Example:</b>
 * <pre>{@code
 * ItemCapacity inventory = new ItemCapacity(9); // 9 slots
 *
 * ItemStack diamonds = new ItemStack(Material.DIAMOND, 32);
 * ItemStack remaining = inventory.insertItem(0, diamonds, false);
 *
 * inventory.save(container);
 * }</pre>
 * <p>
 * All implementations support:
 * <ul>
 *   <li>Insert/extract operations with simulation</li>
 *   <li>Transfer rate limiting</li>
 *   <li>Temperature validation (where applicable)</li>
 *   <li>NBT serialization for persistence</li>
 *   <li>Thread-safe read operations</li>
 * </ul>
 *
 * @see cn.epicmc.foundry.api.Capacity
 * @see cn.epicmc.foundry.common.nbt.NBTSerializable
 */
package cn.epicmc.foundry.common.capacity;
