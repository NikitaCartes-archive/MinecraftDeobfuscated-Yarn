package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.collection.DefaultedList;

public interface CraftingRecipe extends Recipe<CraftingRecipeInput> {
	@Override
	default RecipeType<CraftingRecipe> getType() {
		return RecipeType.CRAFTING;
	}

	@Override
	RecipeSerializer<? extends CraftingRecipe> getSerializer();

	CraftingRecipeCategory getCategory();

	default DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
		return collectRecipeRemainders(input);
	}

	static DefaultedList<ItemStack> collectRecipeRemainders(CraftingRecipeInput input) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			Item item = input.getStackInSlot(i).getItem();
			defaultedList.set(i, item.getRecipeRemainder());
		}

		return defaultedList;
	}

	@Override
	default RecipeBookGroup getRecipeBookTab() {
		return switch (this.getCategory()) {
			case BUILDING -> RecipeBookGroup.CRAFTING_BUILDING_BLOCKS;
			case EQUIPMENT -> RecipeBookGroup.CRAFTING_EQUIPMENT;
			case REDSTONE -> RecipeBookGroup.CRAFTING_REDSTONE;
			case MISC -> RecipeBookGroup.CRAFTING_MISC;
		};
	}
}
