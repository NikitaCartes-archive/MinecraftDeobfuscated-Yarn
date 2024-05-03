package net.minecraft.recipe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class TippedArrowRecipe extends SpecialCraftingRecipe {
	public TippedArrowRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		if (craftingRecipeInput.getWidth() == 3 && craftingRecipeInput.getHeight() == 3) {
			for (int i = 0; i < craftingRecipeInput.getHeight(); i++) {
				for (int j = 0; j < craftingRecipeInput.getWidth(); j++) {
					ItemStack itemStack = craftingRecipeInput.getStackInSlot(j, i);
					if (itemStack.isEmpty()) {
						return false;
					}

					if (j == 1 && i == 1) {
						if (!itemStack.isOf(Items.LINGERING_POTION)) {
							return false;
						}
					} else if (!itemStack.isOf(Items.ARROW)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = craftingRecipeInput.getStackInSlot(1, 1);
		if (!itemStack.isOf(Items.LINGERING_POTION)) {
			return ItemStack.EMPTY;
		} else {
			ItemStack itemStack2 = new ItemStack(Items.TIPPED_ARROW, 8);
			itemStack2.set(DataComponentTypes.POTION_CONTENTS, itemStack.get(DataComponentTypes.POTION_CONTENTS));
			return itemStack2;
		}
	}

	@Override
	public boolean fits(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.TIPPED_ARROW;
	}
}
