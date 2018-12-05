package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MapCloningRecipe extends AbstractRecipe {
	public MapCloningRecipe(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			int i = 0;
			ItemStack itemStack = ItemStack.EMPTY;

			for (int j = 0; j < inventory.getInvSize(); j++) {
				ItemStack itemStack2 = inventory.getInvStack(j);
				if (!itemStack2.isEmpty()) {
					if (itemStack2.getItem() == Items.field_8204) {
						if (!itemStack.isEmpty()) {
							return false;
						}

						itemStack = itemStack2;
					} else {
						if (itemStack2.getItem() != Items.field_8895) {
							return false;
						}

						i++;
					}
				}
			}

			return !itemStack.isEmpty() && i > 0;
		}
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() == Items.field_8204) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.field_8895) {
						return ItemStack.EMPTY;
					}

					i++;
				}
			}
		}

		if (!itemStack.isEmpty() && i >= 1) {
			ItemStack itemStack3 = itemStack.copy();
			itemStack3.setAmount(i + 1);
			return itemStack3;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i >= 3 && j >= 3;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializers.MAP_CLONING;
	}
}
