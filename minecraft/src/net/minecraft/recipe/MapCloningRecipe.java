package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MapCloningRecipe extends SpecialCraftingRecipe {
	public MapCloningRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < craftingInventory.size(); j++) {
			ItemStack itemStack2 = craftingInventory.getStack(j);
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

	public ItemStack craft(CraftingInventory craftingInventory) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < craftingInventory.size(); j++) {
			ItemStack itemStack2 = craftingInventory.getStack(j);
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

		if (!itemStack.isEmpty() && i >= 1) {
			ItemStack itemStack3 = itemStack.copy();
			itemStack3.setCount(i + 1);
			return itemStack3;
		} else {
			return ItemStack.EMPTY;
		}
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
