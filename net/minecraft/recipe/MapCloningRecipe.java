/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    public boolean method_17715(CraftingInventory craftingInventory, World world) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int j = 0; j < craftingInventory.getInvSize(); ++j) {
            ItemStack itemStack2 = craftingInventory.getInvStack(j);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.getItem() == Items.FILLED_MAP) {
                if (!itemStack.isEmpty()) {
                    return false;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() == Items.MAP) {
                ++i;
                continue;
            }
            return false;
        }
        return !itemStack.isEmpty() && i > 0;
    }

    public ItemStack method_17714(CraftingInventory craftingInventory) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int j = 0; j < craftingInventory.getInvSize(); ++j) {
            ItemStack itemStack2 = craftingInventory.getInvStack(j);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.getItem() == Items.FILLED_MAP) {
                if (!itemStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() == Items.MAP) {
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
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i >= 3 && j >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_CLONING;
    }
}

