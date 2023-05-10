package net.minecraft.recipe;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BannerDuplicateRecipe extends SpecialCraftingRecipe {
	public BannerDuplicateRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		DyeColor dyeColor = null;
		ItemStack itemStack = null;
		ItemStack itemStack2 = null;

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack3 = recipeInputInventory.getStack(i);
			if (!itemStack3.isEmpty()) {
				Item item = itemStack3.getItem();
				if (!(item instanceof BannerItem)) {
					return false;
				}

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

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack = recipeInputInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				int j = BannerBlockEntity.getPatternCount(itemStack);
				if (j > 0 && j <= 6) {
					return itemStack.copyWithCount(1);
				}
			}
		}

		return ItemStack.EMPTY;
	}

	public DefaultedList<ItemStack> getRemainder(RecipeInputInventory recipeInputInventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(recipeInputInventory.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = recipeInputInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem().hasRecipeRemainder()) {
					defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
				} else if (itemStack.hasNbt() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
					defaultedList.set(i, itemStack.copyWithCount(1));
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
