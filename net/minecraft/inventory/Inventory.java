/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;

public interface Inventory
extends Clearable {
    public int getInvSize();

    public boolean isInvEmpty();

    public ItemStack getInvStack(int var1);

    public ItemStack takeInvStack(int var1, int var2);

    public ItemStack removeInvStack(int var1);

    public void setInvStack(int var1, ItemStack var2);

    default public int getInvMaxStackAmount() {
        return 64;
    }

    public void markDirty();

    public boolean canPlayerUseInv(PlayerEntity var1);

    default public void onInvOpen(PlayerEntity player) {
    }

    default public void onInvClose(PlayerEntity player) {
    }

    default public boolean isValidInvStack(int slot, ItemStack stack) {
        return true;
    }

    default public int countInInv(Item item) {
        int i = 0;
        for (int j = 0; j < this.getInvSize(); ++j) {
            ItemStack itemStack = this.getInvStack(j);
            if (!itemStack.getItem().equals(item)) continue;
            i += itemStack.getCount();
        }
        return i;
    }

    default public boolean containsAnyInInv(Set<Item> items) {
        for (int i = 0; i < this.getInvSize(); ++i) {
            ItemStack itemStack = this.getInvStack(i);
            if (!items.contains(itemStack.getItem()) || itemStack.getCount() <= 0) continue;
            return true;
        }
        return false;
    }
}

