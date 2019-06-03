/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.DefaultedList;

public class BasicInventory
implements Inventory,
RecipeInputProvider {
    private final int size;
    private final DefaultedList<ItemStack> stackList;
    private List<InventoryListener> listeners;

    public BasicInventory(int i) {
        this.size = i;
        this.stackList = DefaultedList.create(i, ItemStack.EMPTY);
    }

    public BasicInventory(ItemStack ... itemStacks) {
        this.size = itemStacks.length;
        this.stackList = DefaultedList.create(ItemStack.EMPTY, itemStacks);
    }

    public void addListener(InventoryListener inventoryListener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }
        this.listeners.add(inventoryListener);
    }

    public void removeListener(InventoryListener inventoryListener) {
        this.listeners.remove(inventoryListener);
    }

    @Override
    public ItemStack getInvStack(int i) {
        if (i < 0 || i >= this.stackList.size()) {
            return ItemStack.EMPTY;
        }
        return this.stackList.get(i);
    }

    @Override
    public ItemStack takeInvStack(int i, int j) {
        ItemStack itemStack = Inventories.splitStack(this.stackList, i, j);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    public ItemStack method_20631(Item item, int i) {
        ItemStack itemStack = new ItemStack(item, 0);
        for (int j = this.size - 1; j >= 0; --j) {
            ItemStack itemStack2 = this.getInvStack(j);
            if (!itemStack2.getItem().equals(item)) continue;
            int k = i - itemStack.getCount();
            ItemStack itemStack3 = itemStack2.split(k);
            itemStack.increment(itemStack3.getCount());
            if (itemStack.getCount() == i) break;
        }
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    public ItemStack add(ItemStack itemStack) {
        ItemStack itemStack2 = itemStack.copy();
        this.method_20634(itemStack2);
        if (itemStack2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.method_20633(itemStack2);
        if (itemStack2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return itemStack2;
    }

    @Override
    public ItemStack removeInvStack(int i) {
        ItemStack itemStack = this.stackList.get(i);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.stackList.set(i, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        this.stackList.set(i, itemStack);
        if (!itemStack.isEmpty() && itemStack.getCount() > this.getInvMaxStackAmount()) {
            itemStack.setCount(this.getInvMaxStackAmount());
        }
        this.markDirty();
    }

    @Override
    public int getInvSize() {
        return this.size;
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack itemStack : this.stackList) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void markDirty() {
        if (this.listeners != null) {
            for (InventoryListener inventoryListener : this.listeners) {
                inventoryListener.onInvChange(this);
            }
        }
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public void clear() {
        this.stackList.clear();
        this.markDirty();
    }

    @Override
    public void provideRecipeInputs(RecipeFinder recipeFinder) {
        for (ItemStack itemStack : this.stackList) {
            recipeFinder.addItem(itemStack);
        }
    }

    public String toString() {
        return this.stackList.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList()).toString();
    }

    private void method_20633(ItemStack itemStack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack2 = this.getInvStack(i);
            if (!itemStack2.isEmpty()) continue;
            this.setInvStack(i, itemStack.copy());
            itemStack.setCount(0);
            return;
        }
    }

    private void method_20634(ItemStack itemStack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack2 = this.getInvStack(i);
            if (!ItemStack.areItemsEqualIgnoreDamage(itemStack2, itemStack)) continue;
            this.method_20632(itemStack, itemStack2);
            if (!itemStack.isEmpty()) continue;
            return;
        }
    }

    private void method_20632(ItemStack itemStack, ItemStack itemStack2) {
        int i = Math.min(this.getInvMaxStackAmount(), itemStack2.getMaxCount());
        int j = Math.min(itemStack.getCount(), i - itemStack2.getCount());
        if (j > 0) {
            itemStack2.increment(j);
            itemStack.decrement(j);
            this.markDirty();
        }
    }
}

