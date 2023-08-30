package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CookingRecipeCategory;

public class SmokingRecipe extends AbstractCookingRecipe {
	public SmokingRecipe(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
		super(RecipeType.SMOKING, group, category, ingredient, result, experience, cookingTime);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.SMOKER);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMOKING;
	}
}
