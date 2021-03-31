package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class SmokingRecipe extends AbstractCookingRecipe {
	public SmokingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
		super(RecipeType.SMOKING, id, group, input, output, experience, cookTime);
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.SMOKER);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMOKING;
	}
}
