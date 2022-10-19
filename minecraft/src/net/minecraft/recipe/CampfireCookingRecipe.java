package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.util.Identifier;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
	public CampfireCookingRecipe(Identifier id, String group, CookingRecipeCategory category, Ingredient input, ItemStack output, float experience, int cookTime) {
		super(RecipeType.CAMPFIRE_COOKING, id, group, category, input, output, experience, cookTime);
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
