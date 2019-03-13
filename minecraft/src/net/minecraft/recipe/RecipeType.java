package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.cooking.BlastingRecipe;
import net.minecraft.recipe.cooking.CampfireCookingRecipe;
import net.minecraft.recipe.cooking.SmeltingRecipe;
import net.minecraft.recipe.cooking.SmokingRecipe;
import net.minecraft.recipe.crafting.CraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public interface RecipeType<T extends Recipe<?>> {
	RecipeType<CraftingRecipe> CRAFTING = register("crafting");
	RecipeType<SmeltingRecipe> SMELTING = register("smelting");
	RecipeType<BlastingRecipe> BLASTING = register("blasting");
	RecipeType<SmokingRecipe> SMOKING = register("smoking");
	RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");
	RecipeType<StonecuttingRecipe> field_17641 = register("stonecutting");

	static <T extends Recipe<?>> RecipeType<T> register(String string) {
		return Registry.method_10230(Registry.RECIPE_TYPE, new Identifier(string), new RecipeType<T>() {
			public String toString() {
				return string;
			}
		});
	}

	default <C extends Inventory> Optional<T> method_17725(Recipe<C> recipe, World world, C inventory) {
		return recipe.method_8115(inventory, world) ? Optional.of(recipe) : Optional.empty();
	}
}
