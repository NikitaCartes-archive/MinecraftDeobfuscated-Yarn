package net.minecraft.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe extends SpecialCraftingRecipe {
	public ShulkerBoxColoringRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		int i = 0;
		int j = 0;

		for (int k = 0; k < recipeInputInventory.size(); k++) {
			ItemStack itemStack = recipeInputInventory.getStack(k);
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

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		ItemStack itemStack = ItemStack.EMPTY;
		DyeItem dyeItem = (DyeItem)Items.WHITE_DYE;

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack2 = recipeInputInventory.getStack(i);
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
		if (itemStack.hasNbt()) {
			itemStack3.setNbt(itemStack.getNbt().copy());
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
