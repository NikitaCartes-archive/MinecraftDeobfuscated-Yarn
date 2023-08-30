package net.minecraft.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;

public abstract class SpecialCraftingRecipe implements CraftingRecipe {
	private final CraftingRecipeCategory category;

	public SpecialCraftingRecipe(CraftingRecipeCategory category) {
		this.category = category;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	public ItemStack getResult(DynamicRegistryManager registryManager) {
		return ItemStack.EMPTY;
	}

	@Override
	public CraftingRecipeCategory getCategory() {
		return this.category;
	}
}
