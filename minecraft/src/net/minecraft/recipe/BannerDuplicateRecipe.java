package net.minecraft.recipe;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BannerDuplicateRecipe extends SpecialCraftingRecipe {
	public BannerDuplicateRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		DyeColor dyeColor = null;
		ItemStack itemStack = null;
		ItemStack itemStack2 = null;

		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack3 = craftingInventory.getStack(i);
			Item item = itemStack3.getItem();
			if (item instanceof BannerItem) {
				BannerItem bannerItem = (BannerItem)item;
				if (dyeColor == null) {
					dyeColor = bannerItem.getColor();
				} else if (dyeColor != bannerItem.getColor()) {
					return false;
				}

				int j = BannerBlockEntity.getPatternCount(itemStack3);
				if (j > 6) {
					return false;
				}

				if (j > 0) {
					if (itemStack != null) {
						return false;
					}

					itemStack = itemStack3;
				} else {
					if (itemStack2 != null) {
						return false;
					}

					itemStack2 = itemStack3;
				}
			}
		}

		return itemStack != null && itemStack2 != null;
	}

	public ItemStack craft(CraftingInventory craftingInventory) {
		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				int j = BannerBlockEntity.getPatternCount(itemStack);
				if (j > 0 && j <= 6) {
					ItemStack itemStack2 = itemStack.copy();
					itemStack2.setCount(1);
					return itemStack2;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	public DefaultedList<ItemStack> getRemainingStacks(CraftingInventory craftingInventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem().hasRecipeRemainder()) {
					defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
				} else if (itemStack.hasTag() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
					ItemStack itemStack2 = itemStack.copy();
					itemStack2.setCount(1);
					defaultedList.set(i, itemStack2);
				}
			}
		}

		return defaultedList;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.BANNER_DUPLICATE;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}
}
