package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CookingRecipeCategory;

public class SmeltingRecipe extends AbstractCookingRecipe {
	public SmeltingRecipe(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
		super(RecipeType.SMELTING, group, category, ingredient, result, experience, cookingTime);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.FURNACE);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMELTING;
	}
}
