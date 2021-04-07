package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class SmeltingRecipe extends AbstractCookingRecipe {
	public SmeltingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
		super(RecipeType.SMELTING, id, group, input, output, experience, cookTime);
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
