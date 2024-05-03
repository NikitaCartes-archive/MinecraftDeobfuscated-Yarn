package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.world.World;

public class StonecuttingRecipe extends CuttingRecipe {
	public StonecuttingRecipe(String group, Ingredient ingredient, ItemStack result) {
		super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTING, group, ingredient, result);
	}

	public boolean matches(SingleStackRecipeInput singleStackRecipeInput, World world) {
		return this.ingredient.test(singleStackRecipeInput.item());
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.STONECUTTER);
	}
}
