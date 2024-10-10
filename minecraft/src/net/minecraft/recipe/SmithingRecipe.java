package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.world.World;

public interface SmithingRecipe extends Recipe<SmithingRecipeInput> {
	@Override
	default RecipeType<SmithingRecipe> getType() {
		return RecipeType.SMITHING;
	}

	@Override
	RecipeSerializer<? extends SmithingRecipe> getSerializer();

	default boolean matches(SmithingRecipeInput smithingRecipeInput, World world) {
		return Ingredient.matches(this.template(), smithingRecipeInput.template())
			&& Ingredient.matches(this.base(), smithingRecipeInput.base())
			&& Ingredient.matches(this.addition(), smithingRecipeInput.addition());
	}

	Optional<Ingredient> template();

	Optional<Ingredient> base();

	Optional<Ingredient> addition();

	@Override
	default RecipeBookCategory getRecipeBookCategory() {
		return RecipeBookCategories.SMITHING;
	}
}
