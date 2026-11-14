/**
 * NBT serialization utilities for Foundry framework.
 * <p>
 * This package provides type-safe NBT serialization for machine data,
 * block state, and item metadata using Bukkit's PersistentDataContainer API.
 * <p>
 * <b>Core Components:</b>
 * <ul>
 *   <li>{@link cn.epicmc.foundry.common.nbt.NBTSerializable} - Interface for serializable objects</li>
 *   <li>{@link cn.epicmc.foundry.common.nbt.NBTUtil} - Type-safe read/write operations</li>
 *   <li>{@link cn.epicmc.foundry.common.nbt.NBTHelper} - High-level serialization helpers</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * public class EnergyStorage implements NBTSerializable {
 *     private double stored;
 *     private double capacity;
 *
 *     @Override
 *     public void save(PersistentDataContainer container) {
 *         NBTUtil.setDouble(container, "stored", stored);
 *         NBTUtil.setDouble(container, "capacity", capacity);
 *     }
 *
 *     @Override
 *     public void load(PersistentDataContainer container) {
 *         stored = NBTUtil.getDouble(container, "stored", 0.0);
 *         capacity = NBTUtil.getDouble(container, "capacity", 10000.0);
 *     }
 * }
 *
 * // Save to block entity
 * BlockState state = block.getState();
 * if (state instanceof TileState tile) {
 *     PersistentDataContainer container = tile.getPersistentDataContainer();
 *     energyStorage.save(container);
 *     state.update();
 * }
 *
 * // Save to item
 * ItemStack item = new ItemStack(Material.DIAMOND);
 * ItemMeta meta = item.getItemMeta();
 * PersistentDataContainer container = meta.getPersistentDataContainer();
 * energyStorage.save(container);
 * item.setItemMeta(meta);
 * }</pre>
 * <p>
 * <b>Design Principles:</b>
 * <ul>
 *   <li><b>Type Safety:</b> Compile-time type checking prevents runtime errors</li>
 *   <li><b>Namespace Isolation:</b> All keys automatically prefixed with "foundry:"</li>
 *   <li><b>Defensive Reading:</b> Missing data uses defaults, never throws</li>
 *   <li><b>Version Support:</b> Built-in data migration via version fields</li>
 *   <li><b>No NMS:</b> Pure Bukkit API for cross-version compatibility</li>
 * </ul>
 *
 * @see org.bukkit.persistence.PersistentDataContainer
 * @see org.bukkit.block.TileState
 * @see org.bukkit.inventory.meta.ItemMeta
 */
package cn.epicmc.foundry.common.nbt;
