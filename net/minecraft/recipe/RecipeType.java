/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * The recipe type allows matching recipes more efficiently by only checking
 * recipes under a given type.
 * 
 * @param <T> the common supertype of recipes within a recipe type
 */
public interface RecipeType<T extends Recipe<?>> {
    public static final RecipeType<CraftingRecipe> CRAFTING = RecipeType.register("crafting");
    public static final RecipeType<SmeltingRecipe> SMELTING = RecipeType.register("smelting");
    public static final RecipeType<BlastingRecipe> BLASTING = RecipeType.register("blasting");
    public static final RecipeType<SmokingRecipe> SMOKING = RecipeType.register("smoking");
    public static final RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = RecipeType.register("campfire_cooking");
    public static final RecipeType<StonecuttingRecipe> STONECUTTING = RecipeType.register("stonecutting");
    public static final RecipeType<SmithingRecipe> SMITHING = RecipeType.register("smithing");

    public static <T extends Recipe<?>> RecipeType<T> register(final String id) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(id), new RecipeType<T>(){

            public String toString() {
                return id;
            }
        });
    }
}

