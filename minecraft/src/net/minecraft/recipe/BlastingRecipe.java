package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

public class BlastingRecipe extends AbstractCookingRecipe {
	public BlastingRecipe(String string, CookingRecipeCategory cookingRecipeCategory, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		super(string, cookingRecipeCategory, ingredient, itemStack, f, i);
	}

	@Override
	protected Item getCookerItem() {
		return Items.BLAST_FURNACE;
	}

	@Override
	public RecipeSerializer<BlastingRecipe> getSerializer() {
		return RecipeSerializer.BLASTING;
	}

	@Override
	public RecipeType<BlastingRecipe> getType() {
		return RecipeType.BLASTING;
	}

	@Override
	public RecipeBookGroup getRecipeBookTab() {
		return switch (this.getCategory()) {
			case BLOCKS -> RecipeBookGroup.BLAST_FURNACE_BLOCKS;
			case FOOD, MISC -> RecipeBookGroup.BLAST_FURNACE_MISC;
		};
	}
}
