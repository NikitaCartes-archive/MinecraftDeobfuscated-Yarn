package net.minecraft.recipe;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.recipe.book.CraftingRecipeCategory;

public interface CraftingRecipe extends Recipe<RecipeInputInventory> {
	@Override
	default RecipeType<?> getType() {
		return RecipeType.CRAFTING;
	}

	CraftingRecipeCategory getCategory();
}
