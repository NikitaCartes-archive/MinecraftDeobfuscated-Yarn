/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Inventory
extends Clearable {
    public static final int MAX_COUNT_PER_STACK = 64;
    public static final int field_42619 = 8;

    public int size();

    public boolean isEmpty();

    /**
     * Fetches the stack currently stored at the given slot. If the slot is empty,
     * or is outside the bounds of this inventory, returns see {@link ItemStack#EMPTY}.
     */
    public ItemStack getStack(int var1);

    /**
     * Removes a specific number of items from the given slot.
     * 
     * @return the removed items as a stack
     */
    public ItemStack removeStack(int var1, int var2);

    /**
     * Removes the stack currently stored at the indicated slot.
     * 
     * @return the stack previously stored at the indicated slot.
     */
    public ItemStack removeStack(int var1);

    public void setStack(int var1, ItemStack var2);

    /**
     * Returns the maximum number of items a stack can contain when placed inside this inventory.
     * No slots may have more than this number of items. It is effectively the
     * stacking limit for this inventory's slots.
     * 
     * @return the max {@link ItemStack#getCount() count} of item stacks in this inventory
     */
    default public int getMaxCountPerStack() {
        return 64;
    }

    public void markDirty();

    public boolean canPlayerUse(PlayerEntity var1);

    default public void onOpen(PlayerEntity player) {
    }

    default public void onClose(PlayerEntity player) {
    }

    /**
     * Returns whether the given stack is a valid for the indicated slot position.
     */
    default public boolean isValid(int slot, ItemStack stack) {
        return true;
    }

    default public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return true;
    }

    /**
     * Returns the number of times the specified item occurs in this inventory across all stored stacks.
     */
    default public int count(Item item) {
        int i = 0;
        for (int j = 0; j < this.size(); ++j) {
            ItemStack itemStack = this.getStack(j);
            if (!itemStack.getItem().equals(item)) continue;
            i += itemStack.getCount();
        }
        return i;
    }

    /**
     * Determines whether this inventory contains any of the given candidate items.
     */
    default public boolean containsAny(Set<Item> items) {
        return this.containsAny((ItemStack stack) -> !stack.isEmpty() && items.contains(stack.getItem()));
    }

    default public boolean containsAny(Predicate<ItemStack> predicate) {
        for (int i = 0; i < this.size(); ++i) {
            ItemStack itemStack = this.getStack(i);
            if (!predicate.test(itemStack)) continue;
            return true;
        }
        return false;
    }

    public static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player) {
        return Inventory.canPlayerUse(blockEntity, player, 8);
    }

    public static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player, int range) {
        World world = blockEntity.getWorld();
        BlockPos blockPos = blockEntity.getPos();
        if (world == null) {
            return false;
        }
        if (world.getBlockEntity(blockPos) != blockEntity) {
            return false;
        }
        return player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= (double)(range * range);
    }
}

