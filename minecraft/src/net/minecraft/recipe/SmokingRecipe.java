package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;

public class SmokingRecipe extends AbstractCookingRecipe {
	public SmokingRecipe(String string, CookingRecipeCategory cookingRecipeCategory, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		super(string, cookingRecipeCategory, ingredient, itemStack, f, i);
	}

	@Override
	protected Item getCookerItem() {
		return Items.SMOKER;
	}

	@Override
	public RecipeType<SmokingRecipe> getType() {
		return RecipeType.SMOKING;
	}

	@Override
	public RecipeSerializer<SmokingRecipe> getSerializer() {
		return RecipeSerializer.SMOKING;
	}

	@Override
	public RecipeBookCategory getRecipeBookCategory() {
		return RecipeBookCategories.SMOKER_FOOD;
	}
}
