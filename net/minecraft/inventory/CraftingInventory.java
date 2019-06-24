/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.DefaultedList;

public class CraftingInventory
implements Inventory,
RecipeInputProvider {
    private final DefaultedList<ItemStack> stacks;
    private final int width;
    private final int height;
    private final Container container;

    public CraftingInventory(Container container, int i, int j) {
        this.stacks = DefaultedList.ofSize(i * j, ItemStack.EMPTY);
        this.container = container;
        this.width = i;
        this.height = j;
    }

    @Override
    public int getInvSize() {
        return this.stacks.size();
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack itemStack : this.stacks) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getInvStack(int i) {
        if (i >= this.getInvSize()) {
            return ItemStack.EMPTY;
        }
        return this.stacks.get(i);
    }

    @Override
    public ItemStack removeInvStack(int i) {
        return Inventories.removeStack(this.stacks, i);
    }

    @Override
    public ItemStack takeInvStack(int i, int j) {
        ItemStack itemStack = Inventories.splitStack(this.stacks, i, j);
        if (!itemStack.isEmpty()) {
            this.container.onContentChanged(this);
        }
        return itemStack;
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        this.stacks.set(i, itemStack);
        this.container.onContentChanged(this);
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
        this.stacks.clear();
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void provideRecipeInputs(RecipeFinder recipeFinder) {
        for (ItemStack itemStack : this.stacks) {
            recipeFinder.addNormalItem(itemStack);
        }
    }
}

