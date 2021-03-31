/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MapCloningRecipe
extends SpecialCraftingRecipe {
    public MapCloningRecipe(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int j = 0; j < craftingInventory.size(); ++j) {
            ItemStack itemStack2 = craftingInventory.getStack(j);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.isOf(Items.FILLED_MAP)) {
                if (!itemStack.isEmpty()) {
                    return false;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.isOf(Items.MAP)) {
                ++i;
                continue;
            }
            return false;
        }
        return !itemStack.isEmpty() && i > 0;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int j = 0; j < craftingInventory.size(); ++j) {
            ItemStack itemStack2 = craftingInventory.getStack(j);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.isOf(Items.FILLED_MAP)) {
                if (!itemStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.isOf(Items.MAP)) {
                ++i;
                continue;
            }
            return ItemStack.EMPTY;
        }
        if (itemStack.isEmpty() || i < 1) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack3 = itemStack.copy();
        itemStack3.setCount(i + 1);
        return itemStack3;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_CLONING;
    }
}

