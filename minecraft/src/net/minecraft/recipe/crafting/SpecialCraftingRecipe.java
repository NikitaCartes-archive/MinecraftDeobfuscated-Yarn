package net.minecraft.recipe.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public abstract class SpecialCraftingRecipe implements CraftingRecipe {
	private final Identifier field_9009;

	public SpecialCraftingRecipe(Identifier identifier) {
		this.field_9009 = identifier;
	}

	@Override
	public Identifier method_8114() {
		return this.field_9009;
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
