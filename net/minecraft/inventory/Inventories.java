/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public class Inventories {
    public static ItemStack splitStack(List<ItemStack> stacks, int slot, int amount) {
        if (slot < 0 || slot >= stacks.size() || stacks.get(slot).isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        return stacks.get(slot).split(amount);
    }

    public static ItemStack removeStack(List<ItemStack> stacks, int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            return ItemStack.EMPTY;
        }
        return stacks.set(slot, ItemStack.EMPTY);
    }

    public static NbtCompound writeNbt(NbtCompound tag, DefaultedList<ItemStack> stacks) {
        return Inventories.writeNbt(tag, stacks, true);
    }

    public static NbtCompound writeNbt(NbtCompound tag, DefaultedList<ItemStack> stacks, boolean setIfEmpty) {
        NbtList nbtList = new NbtList();
        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack itemStack = stacks.get(i);
            if (itemStack.isEmpty()) continue;
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)i);
            itemStack.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        if (!nbtList.isEmpty() || setIfEmpty) {
            tag.put("Items", nbtList);
        }
        return tag;
    }

    public static void readNbt(NbtCompound tag, DefaultedList<ItemStack> stacks) {
        NbtList nbtList = tag.getList("Items", NbtTypeIds.COMPOUND);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 0xFF;
            if (j < 0 || j >= stacks.size()) continue;
            stacks.set(j, ItemStack.fromNbt(nbtCompound));
        }
    }

    /**
     * Removes a number, not exceeding {@code maxCount}, of items from an inventory based on a predicate and returns that number.
     * @return the number of items removed
     * 
     * @param dryRun whether to return the number of items which would have been removed without actually removing them
     */
    public static int remove(Inventory inventory, Predicate<ItemStack> shouldRemove, int maxCount, boolean dryRun) {
        int i = 0;
        for (int j = 0; j < inventory.size(); ++j) {
            ItemStack itemStack = inventory.getStack(j);
            int k = Inventories.remove(itemStack, shouldRemove, maxCount - i, dryRun);
            if (k > 0 && !dryRun && itemStack.isEmpty()) {
                inventory.setStack(j, ItemStack.EMPTY);
            }
            i += k;
        }
        return i;
    }

    /**
     * Removes a number, not exceeding {@code maxCount}, of items from an item stack based on a predicate and returns that number.
     * @return the number of items removed
     * 
     * @param dryRun whether to return the number of items which would have been removed without actually removing them
     */
    public static int remove(ItemStack stack, Predicate<ItemStack> shouldRemove, int maxCount, boolean dryRun) {
        if (stack.isEmpty() || !shouldRemove.test(stack)) {
            return 0;
        }
        if (dryRun) {
            return stack.getCount();
        }
        int i = maxCount < 0 ? stack.getCount() : Math.min(maxCount, stack.getCount());
        stack.decrement(i);
        return i;
    }
}

