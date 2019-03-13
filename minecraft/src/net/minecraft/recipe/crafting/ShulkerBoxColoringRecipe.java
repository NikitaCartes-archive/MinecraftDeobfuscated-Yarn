package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe extends SpecialCraftingRecipe {
	public ShulkerBoxColoringRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_17734(CraftingInventory craftingInventory, World world) {
		int i = 0;
		int j = 0;

		for (int k = 0; k < craftingInventory.getInvSize(); k++) {
			ItemStack itemStack = craftingInventory.method_5438(k);
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

	public ItemStack method_17733(CraftingInventory craftingInventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		DyeItem dyeItem = (DyeItem)Items.field_8446;

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack2 = craftingInventory.method_5438(i);
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
			itemStack3.method_7980(itemStack.method_7969().method_10553());
		}

		return itemStack3;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public RecipeSerializer<?> method_8119() {
		return RecipeSerializer.field_9041;
	}
}
