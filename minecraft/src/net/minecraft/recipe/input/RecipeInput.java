package net.minecraft.recipe.input;

import net.minecraft.item.ItemStack;

public interface RecipeInput {
	ItemStack getStackInSlot(int slot);

	int size();

	default boolean isEmpty() {
		for (int i = 0; i < this.size(); i++) {
			if (!this.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
