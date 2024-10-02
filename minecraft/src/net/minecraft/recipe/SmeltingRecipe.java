package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

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
	public RecipeBookGroup getRecipeBookTab() {
		return switch (this.getCategory()) {
			case BLOCKS -> RecipeBookGroup.FURNACE_BLOCKS;
			case FOOD -> RecipeBookGroup.FURNACE_FOOD;
			case MISC -> RecipeBookGroup.FURNACE_MISC;
		};
	}
}
