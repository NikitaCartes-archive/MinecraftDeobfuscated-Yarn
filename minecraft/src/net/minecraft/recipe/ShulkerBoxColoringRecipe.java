package net.minecraft.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe extends SpecialCraftingRecipe {
	public ShulkerBoxColoringRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		int i = 0;
		int j = 0;

		for (int k = 0; k < craftingInventory.size(); k++) {
			ItemStack itemStack = craftingInventory.getStack(k);
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

	public ItemStack craft(CraftingInventory craftingInventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		DyeItem dyeItem = (DyeItem)Items.WHITE_DYE;

		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack2 = craftingInventory.getStack(i);
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

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SHULKER_BOX;
	}
}
