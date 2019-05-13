/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class BlastingRecipe
extends AbstractCookingRecipe {
    public BlastingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
        super(RecipeType.BLASTING, identifier, string, ingredient, itemStack, f, i);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(Blocks.BLAST_FURNACE);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BLASTING;
    }
}

