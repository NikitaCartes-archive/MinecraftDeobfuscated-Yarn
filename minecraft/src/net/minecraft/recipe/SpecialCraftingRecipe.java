package net.minecraft.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public abstract class SpecialCraftingRecipe implements CraftingRecipe {
	private final Identifier id;

	public SpecialCraftingRecipe(Identifier identifier) {
		this.id = identifier;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}
}
