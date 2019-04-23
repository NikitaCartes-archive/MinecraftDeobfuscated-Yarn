/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

public interface CraftingRecipe
extends Recipe<CraftingInventory> {
    @Override
    default public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }
}

