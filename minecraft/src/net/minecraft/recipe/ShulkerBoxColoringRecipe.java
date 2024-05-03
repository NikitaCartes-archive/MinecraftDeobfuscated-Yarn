package net.minecraft.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe extends SpecialCraftingRecipe {
	public ShulkerBoxColoringRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		int i = 0;
		int j = 0;

		for (int k = 0; k < craftingRecipeInput.getSize(); k++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(k);
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

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = ItemStack.EMPTY;
		DyeItem dyeItem = (DyeItem)Items.WHITE_DYE;

		for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack2.isEmpty()) {
				Item item = itemStack2.getItem();
				if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
					itemStack = itemStack2;
				} else if (item instanceof DyeItem) {
					dyeItem = (DyeItem)item;
				}
			}
		}

		Block block = ShulkerBoxBlock.get(dyeItem.getColor());
		return itemStack.copyComponentsToNewStack(block, 1);
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
