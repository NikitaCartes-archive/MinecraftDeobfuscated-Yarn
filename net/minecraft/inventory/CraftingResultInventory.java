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
import net.minecraft.util.DefaultedList;
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
    public ItemStack getInvStack(int i) {
        return this.stack.get(0);
    }

    @Override
    public ItemStack takeInvStack(int i, int j) {
        return Inventories.removeStack(this.stack, 0);
    }

    @Override
    public ItemStack removeInvStack(int i) {
        return Inventories.removeStack(this.stack, 0);
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        this.stack.set(0, itemStack);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
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

