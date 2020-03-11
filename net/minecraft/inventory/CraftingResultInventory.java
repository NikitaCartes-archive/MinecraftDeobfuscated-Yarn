/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class CraftingResultInventory
implements Inventory,
RecipeUnlocker {
    private final DefaultedList<ItemStack> stack = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private Recipe<?> lastRecipe;

    @Override
    public int getInvSize() {
        return 1;
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack itemStack : this.stack) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return this.stack.get(0);
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        return Inventories.removeStack(this.stack, 0);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        return Inventories.removeStack(this.stack, 0);
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        this.stack.set(0, stack);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stack.clear();
    }

    @Override
    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        this.lastRecipe = recipe;
    }

    @Override
    @Nullable
    public Recipe<?> getLastRecipe() {
        return this.lastRecipe;
    }
}

