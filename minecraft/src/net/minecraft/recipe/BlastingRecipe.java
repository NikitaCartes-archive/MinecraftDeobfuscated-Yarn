package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class BlastingRecipe extends AbstractCookingRecipe {
	public BlastingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
		super(RecipeType.BLASTING, id, group, input, output, experience, cookTime);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.BLAST_FURNACE);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.BLASTING;
	}
}
