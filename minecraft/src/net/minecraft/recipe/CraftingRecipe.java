package net.minecraft.recipe;

import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;

public interface CraftingRecipe extends Recipe<CraftingRecipeInput> {
	@Override
	default RecipeType<?> getType() {
		return RecipeType.CRAFTING;
	}

	CraftingRecipeCategory getCategory();
}
