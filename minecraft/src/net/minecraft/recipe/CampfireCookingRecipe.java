package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CookingRecipeCategory;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
	public CampfireCookingRecipe(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
		super(RecipeType.CAMPFIRE_COOKING, group, category, ingredient, result, experience, cookingTime);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.CAMPFIRE);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.CAMPFIRE_COOKING;
	}
}
