/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;

public final class StackMappingInventory {
    private int topSlot = -1;
    private final boolean[] emptySlots;
    private final int[] slotMap;
    private final ItemStack[] stacks;
    private final int size;

    public StackMappingInventory(int size) {
        this.size = size;
        this.emptySlots = new boolean[size];
        Arrays.fill(this.emptySlots, true);
        this.slotMap = new int[size];
        this.stacks = new ItemStack[size];
    }

    private int findEmptySlot() {
        for (int i = 0; i < this.emptySlots.length; ++i) {
            if (!this.emptySlots[i]) continue;
            return i;
        }
        return -1;
    }

    public boolean addStack(ItemStack stack, int slot) {
        if (this.topSlot == this.size - 1 || stack.isEmpty()) {
            return false;
        }
        ++this.topSlot;
        this.slotMap[this.topSlot] = slot;
        this.emptySlots[slot] = false;
        this.stacks[this.topSlot] = stack;
        return true;
    }

    public boolean addStack(ItemStack stack) {
        return this.addStack(stack, this.findEmptySlot());
    }

    public ItemStack removeTopStack() {
        if (this.topSlot == -1) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = this.stacks[this.topSlot];
        this.stacks[this.topSlot] = ItemStack.EMPTY;
        this.emptySlots[this.slotMap[this.topSlot]] = true;
        --this.topSlot;
        return itemStack;
    }

    public boolean hasSlot(int slot) {
        return slot >= 0 && slot <= this.size - 1;
    }

    public boolean setStack(ItemStack stack, int slot) {
        if (slot < 0 || slot > this.size - 1) {
            return false;
        }
        if (stack.isEmpty()) {
            this.removeStack(slot);
            return true;
        }
        for (int i = 0; i < this.getItemCount(); ++i) {
            if (this.slotMap[i] != slot) continue;
            this.stacks[i] = stack;
            return true;
        }
        return this.addStack(stack, slot);
    }

    public ItemStack getStack(int slot) {
        for (int i = 0; i < this.getItemCount(); ++i) {
            if (this.slotMap[i] != slot) continue;
            return this.stacks[i];
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeStack(int slot) {
        for (int i = 0; i < this.getItemCount(); ++i) {
            if (this.slotMap[i] != slot) continue;
            ItemStack itemStack = this.stacks[i];
            if (i != this.topSlot) {
                System.arraycopy(this.slotMap, i + 1, this.slotMap, i, this.topSlot - i);
                System.arraycopy(this.stacks, i + 1, this.stacks, i, this.topSlot - i);
            }
            this.emptySlots[slot] = true;
            --this.topSlot;
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    public int getItemCount() {
        return this.topSlot + 1;
    }

    public boolean isFull() {
        return this.getItemCount() == this.size;
    }

    public boolean isEmpty() {
        return this.topSlot == -1;
    }

    public List<ItemStack> getStacks() {
        ItemStack[] itemStacks = new ItemStack[this.getItemCount()];
        System.arraycopy(this.stacks, 0, itemStacks, 0, this.getItemCount());
        return List.of(itemStacks);
    }

    public List<ItemStack> clear() {
        if (this.topSlot == -1) {
            return List.of();
        }
        List<ItemStack> list = this.getStacks();
        for (int i = 0; i < this.getItemCount(); ++i) {
            this.stacks[i] = ItemStack.EMPTY;
            this.emptySlots[i] = true;
        }
        this.topSlot = -1;
        return list;
    }

    public FlatteningType flatten() {
        FlatteningType flatteningType = FlatteningType.NO_CHANGE;
        for (int i = 0; i < this.getItemCount(); ++i) {
            for (int j = this.stacks[i].getCount() - 1; j > 0; --j) {
                flatteningType = FlatteningType.FULLY_FLATTENED;
                if (this.addStack(this.stacks[i].split(1))) continue;
                this.stacks[i].increment(1);
                return FlatteningType.PARTIALLY_FLATTENED;
            }
        }
        return flatteningType;
    }

    public static enum FlatteningType {
        PARTIALLY_FLATTENED,
        FULLY_FLATTENED,
        NO_CHANGE;

    }
}

