/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BookCloningRecipe
extends SpecialCraftingRecipe {
    public BookCloningRecipe(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int j = 0; j < craftingInventory.size(); ++j) {
            ItemStack itemStack2 = craftingInventory.getStack(j);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.isOf(Items.WRITTEN_BOOK)) {
                if (!itemStack.isEmpty()) {
                    return false;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.isOf(Items.WRITABLE_BOOK)) {
                ++i;
                continue;
            }
            return false;
        }
        return !itemStack.isEmpty() && itemStack.hasTag() && i > 0;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int j = 0; j < craftingInventory.size(); ++j) {
            ItemStack itemStack2 = craftingInventory.getStack(j);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.isOf(Items.WRITTEN_BOOK)) {
                if (!itemStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.isOf(Items.WRITABLE_BOOK)) {
                ++i;
                continue;
            }
            return ItemStack.EMPTY;
        }
        if (itemStack.isEmpty() || !itemStack.hasTag() || i < 1 || WrittenBookItem.getGeneration(itemStack) >= 2) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack3 = new ItemStack(Items.WRITTEN_BOOK, i);
        NbtCompound nbtCompound = itemStack.getTag().copy();
        nbtCompound.putInt("generation", WrittenBookItem.getGeneration(itemStack) + 1);
        itemStack3.setTag(nbtCompound);
        return itemStack3;
    }

    @Override
    public DefaultedList<ItemStack> getRemainingStacks(CraftingInventory craftingInventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (itemStack.getItem().hasRecipeRemainder()) {
                defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
                continue;
            }
            if (!(itemStack.getItem() instanceof WrittenBookItem)) continue;
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(1);
            defaultedList.set(i, itemStack2);
            break;
        }
        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BOOK_CLONING;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
    }
}

