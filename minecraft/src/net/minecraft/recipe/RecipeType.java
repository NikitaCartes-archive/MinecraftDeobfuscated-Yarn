package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

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
		return Registry.register(Registry.RECIPE_TYPE, new Identifier(id), new RecipeType<T>() {
			public String toString() {
				return id;
			}
		});
	}

	/**
	 * {@return the given {@code recipe} if it matches, otherwise empty}
	 * 
	 * <p>This utility method casts the {@code recipe} from {@code Recipe<C>} to
	 * {@code T} conveniently.
	 * 
	 * @param recipe the recipe to match and cast
	 * @param world the input world
	 * @param inventory the input inventory
	 */
	default <C extends Inventory> Optional<T> match(Recipe<C> recipe, World world, C inventory) {
		return recipe.matches(inventory, world) ? Optional.of(recipe) : Optional.empty();
	}
}
