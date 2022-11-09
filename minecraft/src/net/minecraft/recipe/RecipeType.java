package net.minecraft.recipe;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * The recipe type allows matching recipes more efficiently by only checking
 * recipes under a given type.
 * 
 * @param <T> the common supertype of recipes within a recipe type
 */
public interface RecipeType<T extends Recipe<?>> {
	RecipeType<CraftingRecipe> CRAFTING = register("crafting");
	RecipeType<SmeltingRecipe> SMELTING = register("smelting");
	RecipeType<BlastingRecipe> BLASTING = register("blasting");
	RecipeType<SmokingRecipe> SMOKING = register("smoking");
	RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");
	RecipeType<StonecuttingRecipe> STONECUTTING = register("stonecutting");
	RecipeType<SmithingRecipe> SMITHING = register("smithing");

	static <T extends Recipe<?>> RecipeType<T> register(String id) {
		return Registry.register(Registries.RECIPE_TYPE, new Identifier(id), new RecipeType<T>() {
			public String toString() {
				return id;
			}
		});
	}
}
