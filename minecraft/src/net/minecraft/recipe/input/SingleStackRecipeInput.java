package net.minecraft.recipe.input;

import net.minecraft.item.ItemStack;

public record SingleStackRecipeInput(ItemStack item) implements RecipeInput {
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot != 0) {
			throw new IllegalArgumentException("No item for index " + slot);
		} else {
			return this.item;
		}
	}

	@Override
	public int getSize() {
		return 1;
	}
}
