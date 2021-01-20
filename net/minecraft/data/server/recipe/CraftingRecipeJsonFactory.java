/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.RecipeJsonProvider;

public interface CraftingRecipeJsonFactory {
    public CraftingRecipeJsonFactory criterion(String var1, CriterionConditions var2);

    public CraftingRecipeJsonFactory group(String var1);

    public void offerTo(Consumer<RecipeJsonProvider> var1);
}

