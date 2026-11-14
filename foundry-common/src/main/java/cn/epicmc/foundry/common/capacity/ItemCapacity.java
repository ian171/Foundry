package cn.epicmc.foundry.common.capacity;

import cn.epicmc.foundry.api.Capacity;
import cn.epicmc.foundry.api.unit.Unit;
import cn.epicmc.foundry.common.nbt.NBTHelper;
import cn.epicmc.foundry.common.nbt.NBTSerializable;
import cn.epicmc.foundry.common.nbt.NBTUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Item storage implementation of Capacity.
 * <p>
 * This capacity type stores ItemStacks in slots, similar to a chest or machine inventory.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Slot-based storage (like furnace input/output)</li>
 *   <li>Stack size limits per slot</li>
 *   <li>Item filtering and validation</li>
 *   <li>NBT persistence (save to blocks or items)</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Create 9-slot inventory
 * ItemCapacity inventory = new ItemCapacity(9);
 *
 * // Insert items
 * ItemStack diamonds = new ItemStack(Material.DIAMOND, 32);
 * ItemStack remaining = inventory.insertItem(0, diamonds, false);
 *
 * // Extract items
 * ItemStack extracted = inventory.extractItem(0, 16, false);
 *
 * // Save to NBT
 * PersistentDataContainer container = ...;
 * inventory.save(container);
 * }</pre>
 */
public class ItemCapacity implements NBTSerializable {

    private final List<ItemStack> slots;
    private final int slotCount;
    private int maxStackSize;

    /**
     * Create item capacity with default stack size (64).
     *
     * @param slotCount Number of slots
     */
    public ItemCapacity(int slotCount) {
        this(slotCount, 64);
    }

    /**
     * Create item capacity with custom stack size.
     *
     * @param slotCount Number of slots
     * @param maxStackSize Maximum stack size per slot
     */
    public ItemCapacity(int slotCount, int maxStackSize) {
        if (slotCount <= 0) {
            throw new IllegalArgumentException("Slot count must be positive");
        }
        this.slotCount = slotCount;
        this.maxStackSize = maxStackSize;
        this.slots = new ArrayList<>(slotCount);
        for (int i = 0; i < slotCount; i++) {
            slots.add(null);
        }
    }

    // ==================== Slot Operations ====================

    /**
     * Get the number of slots.
     */
    public int getSlotCount() {
        return slotCount;
    }

    /**
     * Get the item in a specific slot.
     *
     * @param slot Slot index
     * @return ItemStack in slot, or null if empty
     */
    public ItemStack getStackInSlot(int slot) {
        validateSlot(slot);
        ItemStack stack = slots.get(slot);
        return stack != null ? stack.clone() : null;
    }

    /**
     * Set the item in a specific slot.
     * <p>
     * <b>Warning:</b> This directly sets the slot, bypassing validation.
     * Use for loading data only.
     *
     * @param slot Slot index
     * @param stack ItemStack to set (will be cloned)
     */
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlot(slot);
        slots.set(slot, stack != null ? stack.clone() : null);
    }

    /**
     * Insert item into a specific slot.
     *
     * @param slot Slot index
     * @param stack ItemStack to insert
     * @param simulate If true, don't modify the inventory
     * @return ItemStack that couldn't be inserted (empty if all inserted)
     */
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        validateSlot(slot);

        if (stack == null || stack.getType().isAir()) {
            return null;
        }

        ItemStack existing = slots.get(slot);

        // Empty slot
        if (existing == null || existing.getType().isAir()) {
            int toInsert = Math.min(stack.getAmount(), maxStackSize);
            if (!simulate) {
                ItemStack inserted = stack.clone();
                inserted.setAmount(toInsert);
                slots.set(slot, inserted);
            }

            if (stack.getAmount() > toInsert) {
                ItemStack remainder = stack.clone();
                remainder.setAmount(stack.getAmount() - toInsert);
                return remainder;
            }
            return null;
        }

        // Slot has item - check if stackable
        if (!existing.isSimilar(stack)) {
            return stack; // Can't insert, different item
        }

        // Calculate how much we can insert
        int maxInsert = maxStackSize - existing.getAmount();
        if (maxInsert <= 0) {
            return stack; // Slot full
        }

        int toInsert = Math.min(stack.getAmount(), maxInsert);

        if (!simulate) {
            existing.setAmount(existing.getAmount() + toInsert);
        }

        if (stack.getAmount() > toInsert) {
            ItemStack remainder = stack.clone();
            remainder.setAmount(stack.getAmount() - toInsert);
            return remainder;
        }

        return null;
    }

    /**
     * Extract item from a specific slot.
     *
     * @param slot Slot index
     * @param amount Amount to extract
     * @param simulate If true, don't modify the inventory
     * @return ItemStack extracted (null if nothing)
     */
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlot(slot);

        if (amount <= 0) {
            return null;
        }

        ItemStack existing = slots.get(slot);
        if (existing == null || existing.getType().isAir()) {
            return null;
        }

        int toExtract = Math.min(amount, existing.getAmount());
        ItemStack extracted = existing.clone();
        extracted.setAmount(toExtract);

        if (!simulate) {
            if (toExtract >= existing.getAmount()) {
                slots.set(slot, null); // Slot now empty
            } else {
                existing.setAmount(existing.getAmount() - toExtract);
            }
        }

        return extracted;
    }

    /**
     * Check if a slot is empty.
     */
    public boolean isSlotEmpty(int slot) {
        validateSlot(slot);
        ItemStack stack = slots.get(slot);
        return stack == null || stack.getType().isAir();
    }

    /**
     * Clear a specific slot.
     */
    public void clearSlot(int slot) {
        validateSlot(slot);
        slots.set(slot, null);
    }

    /**
     * Clear all slots.
     */
    public void clearAll() {
        for (int i = 0; i < slotCount; i++) {
            slots.set(i, null);
        }
    }

    /**
     * Get maximum stack size per slot.
     */
    public int getMaxStackSize() {
        return maxStackSize;
    }

    /**
     * Set maximum stack size per slot.
     */
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    // ==================== Utility Methods ====================

    /**
     * Count total items of a specific type.
     */
    public int countItems(ItemStack template) {
        if (template == null) {
            return 0;
        }

        int count = 0;
        for (ItemStack stack : slots) {
            if (stack != null && stack.isSimilar(template)) {
                count += stack.getAmount();
            }
        }
        return count;
    }

    /**
     * Check if inventory contains at least the specified amount.
     */
    public boolean contains(ItemStack template, int amount) {
        return countItems(template) >= amount;
    }

    /**
     * Find first slot containing the item.
     *
     * @return Slot index, or -1 if not found
     */
    public int findSlot(ItemStack template) {
        if (template == null) {
            return -1;
        }

        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = slots.get(i);
            if (stack != null && stack.isSimilar(template)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find first empty slot.
     *
     * @return Slot index, or -1 if no empty slots
     */
    public int findEmptySlot() {
        for (int i = 0; i < slotCount; i++) {
            if (isSlotEmpty(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Auto-insert item into any available slot.
     *
     * @param stack Item to insert
     * @param simulate If true, don't modify inventory
     * @return Remainder that couldn't be inserted
     */
    public ItemStack insertItemAuto(ItemStack stack, boolean simulate) {
        if (stack == null || stack.getType().isAir()) {
            return null;
        }

        ItemStack remaining = stack.clone();

        // First pass: try to stack with existing items
        for (int i = 0; i < slotCount && remaining != null; i++) {
            remaining = insertItem(i, remaining, simulate);
        }

        return remaining;
    }

    private void validateSlot(int slot) {
        if (slot < 0 || slot >= slotCount) {
            throw new IndexOutOfBoundsException(
                "Slot " + slot + " out of range [0, " + slotCount + ")"
            );
        }
    }

    // ==================== NBT Serialization ====================

    @Override
    public void save(PersistentDataContainer container) {
        NBTUtil.setInt(container, "item_capacity_version", 1);
        NBTUtil.setInt(container, "slot_count", slotCount);
        NBTUtil.setInt(container, "max_stack_size", maxStackSize);

        // Save each slot
        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = slots.get(i);
            if (stack != null && !stack.getType().isAir()) {
                NBTHelper.saveItemStack(container, "slot_" + i, stack);
            }
        }
    }

    @Override
    public void load(PersistentDataContainer container) {
        int version = NBTUtil.getInt(container, "item_capacity_version", 1);

        switch (version) {
            case 1 -> loadV1(container);
            default -> throw new IllegalStateException("Unknown item capacity version: " + version);
        }
    }

    private void loadV1(PersistentDataContainer container) {
        this.maxStackSize = NBTUtil.getInt(container, "max_stack_size", 64);

        // Load each slot
        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = NBTHelper.loadItemStack(container, "slot_" + i);
            slots.set(i, stack);
        }
    }

    @Override
    public String toString() {
        int occupiedSlots = 0;
        int totalItems = 0;

        for (ItemStack stack : slots) {
            if (stack != null && !stack.getType().isAir()) {
                occupiedSlots++;
                totalItems += stack.getAmount();
            }
        }

        return String.format("ItemCapacity{%d/%d slots, %d items}",
            occupiedSlots, slotCount, totalItems);
    }
}
