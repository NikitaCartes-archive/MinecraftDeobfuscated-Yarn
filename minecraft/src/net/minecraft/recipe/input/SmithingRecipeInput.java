package net.minecraft.recipe.input;

import net.minecraft.item.ItemStack;

public record SmithingRecipeInput(ItemStack template, ItemStack base, ItemStack addition) implements RecipeInput {
	@Override
	public ItemStack getStackInSlot(int slot) {
		return switch (slot) {
			case 0 -> this.template;
			case 1 -> this.base;
			case 2 -> this.addition;
			default -> throw new IllegalArgumentException("Recipe does not contain slot " + slot);
		};
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public boolean isEmpty() {
		return this.template.isEmpty() && this.base.isEmpty() && this.addition.isEmpty();
	}
}
