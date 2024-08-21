package net.minecraft.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;

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
	public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public CraftingRecipeCategory getCategory() {
		return this.category;
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		return IngredientPlacement.NONE;
	}
}
