package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe extends AbstractRecipe {
	public ShulkerBoxColoringRecipe(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			int i = 0;
			int j = 0;

			for (int k = 0; k < inventory.getInvSize(); k++) {
				ItemStack itemStack = inventory.getInvStack(k);
				if (!itemStack.isEmpty()) {
					if (Block.getBlockFromItem(itemStack.getItem()) instanceof ShulkerBoxBlock) {
						i++;
					} else {
						if (!(itemStack.getItem() instanceof DyeItem)) {
							return false;
						}

						j++;
					}

					if (j > 1 || i > 1) {
						return false;
					}
				}
			}

			return i == 1 && j == 1;
		}
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		DyeItem dyeItem = (DyeItem)Items.field_8446;

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (!itemStack2.isEmpty()) {
				Item item = itemStack2.getItem();
				if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
					itemStack = itemStack2;
				} else if (item instanceof DyeItem) {
					dyeItem = (DyeItem)item;
				}
			}
		}

		ItemStack itemStack3 = ShulkerBoxBlock.getItemStack(dyeItem.getColor());
		if (itemStack.hasTag()) {
			itemStack3.setTag(itemStack.getTag().copy());
		}

		return itemStack3;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializers.SHULKER_BOX;
	}
}
