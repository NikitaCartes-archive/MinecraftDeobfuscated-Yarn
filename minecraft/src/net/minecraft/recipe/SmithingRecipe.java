package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface SmithingRecipe extends Recipe<Inventory> {
	@Override
	default RecipeType<?> getType() {
		return RecipeType.SMITHING;
	}

	@Override
	default boolean fits(int width, int height) {
		return width >= 3 && height >= 1;
	}

	@Override
	default ItemStack createIcon() {
		return new ItemStack(Blocks.SMITHING_TABLE);
	}

	boolean testTemplate(ItemStack stack);

	boolean testBase(ItemStack stack);

	boolean testAddition(ItemStack stack);
}
