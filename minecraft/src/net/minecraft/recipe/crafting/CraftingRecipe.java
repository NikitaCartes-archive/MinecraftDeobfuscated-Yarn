package net.minecraft.recipe.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

public interface CraftingRecipe extends Recipe<CraftingInventory> {
	@Override
	default RecipeType<?> getType() {
		return RecipeType.CRAFTING;
	}
}
