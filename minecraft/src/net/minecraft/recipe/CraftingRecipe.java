package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.book.CraftingRecipeCategory;

public interface CraftingRecipe extends Recipe<CraftingInventory> {
	@Override
	default RecipeType<?> getType() {
		return RecipeType.CRAFTING;
	}

	CraftingRecipeCategory getCategory();
}
