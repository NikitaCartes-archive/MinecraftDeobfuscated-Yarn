/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class DoubleInventory
implements Inventory {
    private final Inventory first;
    private final Inventory second;

    public DoubleInventory(Inventory inventory, Inventory inventory2) {
        this.first = inventory;
        this.second = inventory2;
    }

    @Override
    public int size() {
        return this.first.size() + this.second.size();
    }

    @Override
    public boolean isEmpty() {
        return this.first.isEmpty() && this.second.isEmpty();
    }

    public boolean isPart(Inventory inventory) {
        return this.first == inventory || this.second == inventory;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot >= this.first.size()) {
            return this.second.getStack(slot - this.first.size());
        }
        return this.first.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot >= this.first.size()) {
            return this.second.removeStack(slot - this.first.size(), amount);
        }
        return this.first.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot >= this.first.size()) {
            return this.second.removeStack(slot - this.first.size());
        }
        return this.first.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= this.first.size()) {
            this.second.setStack(slot - this.first.size(), stack);
        } else {
            this.first.setStack(slot, stack);
        }
    }

    @Override
    public int getMaxCountPerStack() {
        return this.first.getMaxCountPerStack();
    }

    @Override
    public void markDirty() {
        this.first.markDirty();
        this.second.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.first.canPlayerUse(player) && this.second.canPlayerUse(player);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.first.onOpen(player);
        this.second.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        this.first.onClose(player);
        this.second.onClose(player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot >= this.first.size()) {
            return this.second.isValid(slot - this.first.size(), stack);
        }
        return this.first.isValid(slot, stack);
    }

    @Override
    public void clear() {
        this.first.clear();
        this.second.clear();
    }
}

