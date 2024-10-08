package net.minecraft.recipe;

import net.minecraft.component.DataComponentTypes;
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
		if (craftingRecipeInput.getStackCount() < 2) {
			return false;
		} else {
			boolean bl = false;
			boolean bl2 = false;

			for (int i = 0; i < craftingRecipeInput.size(); i++) {
				ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
				if (!itemStack.isEmpty()) {
					if (itemStack.contains(DataComponentTypes.MAP_ID)) {
						if (bl2) {
							return false;
						}

						bl2 = true;
					} else {
						if (!itemStack.isOf(Items.MAP)) {
							return false;
						}

						bl = true;
					}
				}
			}

			return bl2 && bl;
		}
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < craftingRecipeInput.size(); j++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.contains(DataComponentTypes.MAP_ID)) {
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
	public RecipeSerializer<MapCloningRecipe> getSerializer() {
		return RecipeSerializer.MAP_CLONING;
	}
}
