package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;

public interface CraftingRecipe extends Recipe<CraftingInventory> {
	@Override
	default RecipeType<?> getType() {
		return RecipeType.CRAFTING;
	}
}
