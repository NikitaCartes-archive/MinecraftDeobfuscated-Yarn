package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;

public class SmeltingRecipe extends AbstractCookingRecipe {
	public SmeltingRecipe(String string, CookingRecipeCategory cookingRecipeCategory, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		super(string, cookingRecipeCategory, ingredient, itemStack, f, i);
	}

	@Override
	protected Item getCookerItem() {
		return Items.FURNACE;
	}

	@Override
	public RecipeSerializer<SmeltingRecipe> getSerializer() {
		return RecipeSerializer.SMELTING;
	}

	@Override
	public RecipeType<SmeltingRecipe> getType() {
		return RecipeType.SMELTING;
	}

	@Override
	public RecipeBookCategory getRecipeBookTab() {
		return switch (this.getCategory()) {
			case BLOCKS -> RecipeBookCategories.FURNACE_BLOCKS;
			case FOOD -> RecipeBookCategories.FURNACE_FOOD;
			case MISC -> RecipeBookCategories.FURNACE_MISC;
		};
	}
}
