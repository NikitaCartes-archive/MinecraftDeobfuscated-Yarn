package net.minecraft.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class MapCloningRecipe extends SpecialCraftingRecipe {
	public MapCloningRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < craftingRecipeInput.getSize(); j++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.isOf(Items.FILLED_MAP)) {
					if (!itemStack.isEmpty()) {
						return false;
					}

					itemStack = itemStack2;
				} else {
					if (!itemStack2.isOf(Items.MAP)) {
						return false;
					}

					i++;
				}
			}
		}

		return !itemStack.isEmpty() && i > 0;
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < craftingRecipeInput.getSize(); j++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.isOf(Items.FILLED_MAP)) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2;
				} else {
					if (!itemStack2.isOf(Items.MAP)) {
						return ItemStack.EMPTY;
					}

					i++;
				}
			}
		}

		return !itemStack.isEmpty() && i >= 1 ? itemStack.copyWithCount(i + 1) : ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.MAP_CLONING;
	}
}
