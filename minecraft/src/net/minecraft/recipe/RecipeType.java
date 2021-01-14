package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public interface RecipeType<T extends Recipe<?>> {
	RecipeType<CraftingRecipe> CRAFTING = register("crafting");
	RecipeType<SmeltingRecipe> SMELTING = register("smelting");
	RecipeType<BlastingRecipe> BLASTING = register("blasting");
	RecipeType<SmokingRecipe> SMOKING = register("smoking");
	RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");
	RecipeType<StonecuttingRecipe> STONECUTTING = register("stonecutting");
	RecipeType<SmithingRecipe> SMITHING = register("smithing");

	static <T extends Recipe<?>> RecipeType<T> register(String id) {
		return Registry.register(Registry.RECIPE_TYPE, new Identifier(id), new RecipeType<T>() {
			public String toString() {
				return id;
			}
		});
	}

	default <C extends Inventory> Optional<T> get(Recipe<C> recipe, World world, C inventory) {
		return recipe.matches(inventory, world) ? Optional.of(recipe) : Optional.empty();
	}
}
