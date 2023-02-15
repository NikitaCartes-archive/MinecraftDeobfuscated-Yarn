/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface SingleStackInventory
extends Inventory {
    @Override
    default public int size() {
        return 1;
    }

    @Override
    default public boolean isEmpty() {
        return this.getStack().isEmpty();
    }

    @Override
    default public void clear() {
        this.removeStack();
    }

    default public ItemStack getStack() {
        return this.getStack(0);
    }

    default public ItemStack removeStack() {
        return this.removeStack(0);
    }

    default public void setStack(ItemStack stack) {
        this.setStack(0, stack);
    }

    @Override
    default public ItemStack removeStack(int slot) {
        return this.removeStack(slot, this.getMaxCountPerStack());
    }
}

