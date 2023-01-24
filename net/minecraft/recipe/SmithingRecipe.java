/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

public interface SmithingRecipe
extends Recipe<Inventory> {
    @Override
    default public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    default public boolean fits(int width, int height) {
        return width >= 3 && height >= 1;
    }

    @Override
    default public ItemStack createIcon() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    public boolean testTemplate(ItemStack var1);

    public boolean testBase(ItemStack var1);

    public boolean testAddition(ItemStack var1);
}

