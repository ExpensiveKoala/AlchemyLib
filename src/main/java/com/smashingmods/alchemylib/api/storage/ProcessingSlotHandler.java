package com.smashingmods.alchemylib.api.storage;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;

/**
 * This class is a wrapper for {@link ItemStackHandler} that provides helper methods for
 */
@SuppressWarnings("unused")
public class ProcessingSlotHandler extends ItemStackHandler {

    public ProcessingSlotHandler(int size) {
        super(size);
    }

    /**
     * Increments the count of the ItemStack in the given slot of this handler by the ammount.
     */
    public void incrementSlot(int slot, int amount) {
        ItemStack temp = this.getStackInSlot(slot);

        if (temp.getCount() + amount <= temp.getMaxStackSize()) {
            temp.setCount(temp.getCount() + amount);
        }

        this.setStackInSlot(slot, temp);
    }

    /**
     * Sets an ItemStack into this handler's slot. If the slot already has that
     * ItemStack, it increments it by the ItemStack's count. If that slot isn't empty,
     * this method does nothing.
     *
     * @param slot Integer value representing this item handler's slot.
     * @param itemStack {@link ItemStack}
     */
    public void setOrIncrement(int slot, ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            if (getStackInSlot(slot).isEmpty()) {
                setStackInSlot(slot, itemStack);
            } else {
                incrementSlot(slot, itemStack.getCount());
            }
        }
    }

    /**
     * This method is used to decrement the count of the ItemStack in this handler's slot.
     * If it decrements to 0, the slot's ItemStack is set to EMPTY.
     *
     * @param slot Integer value representing this item handler's slot.
     * @param amount Integer value for how much to decrease the size of the ItemStack in the slot.
     */
    public void decrementSlot(int slot, int amount) {
        ItemStack temp = this.getStackInSlot(slot);

        if (temp.isEmpty()) return;
        if (temp.getCount() - amount < 0) return;

        temp.shrink(amount);
        if (temp.getCount() <= 0) {
            this.setStackInSlot(slot, ItemStack.EMPTY);
        } else {
            this.setStackInSlot(slot, temp);
        }
    }

    /**
     * Empties the ItemStacks held by this handler into the Inventory passed to the method.
     * This method doesn't check to see if the Inventory can hold more items at any step
     * of the process. Consumers of this method will need to verify that the Inventory has
     * space for all ItemStacks held by this handler.
     *
     * @param inventory {@link Inventory}
     */
    public void emptyToInventory(Inventory inventory) {
        for (int i = 0; i < this.stacks.size(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                ItemHandlerHelper.insertItemStacked(new PlayerInvWrapper(inventory), getStackInSlot(i), false);
                setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    /**
     * Tests to see if the stacks held by this handler are all empty.
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return stacks.stream().allMatch(ItemStack::isEmpty);
    }

    /**
     * @return NonNullList of ItemStack contained by this handler.
     */
    public NonNullList<ItemStack> getStacks() {
        return this.stacks;
    }
}
