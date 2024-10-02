package net.minecraft.recipe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BannerDuplicateRecipe extends SpecialCraftingRecipe {
	public BannerDuplicateRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		DyeColor dyeColor = null;
		ItemStack itemStack = null;
		ItemStack itemStack2 = null;

		for (int i = 0; i < craftingRecipeInput.size(); i++) {
			ItemStack itemStack3 = craftingRecipeInput.getStackInSlot(i);
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

				int j = itemStack3.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT).layers().size();
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

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		for (int i = 0; i < craftingRecipeInput.size(); i++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				int j = itemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT).layers().size();
				if (j > 0 && j <= 6) {
					return itemStack.copyWithCount(1);
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = input.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				ItemStack itemStack2 = itemStack.getItem().getRecipeRemainder();
				if (!itemStack2.isEmpty()) {
					defaultedList.set(i, itemStack2);
				} else if (!itemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT).layers().isEmpty()) {
					defaultedList.set(i, itemStack.copyWithCount(1));
				}
			}
		}

		return defaultedList;
	}

	@Override
	public RecipeSerializer<BannerDuplicateRecipe> getSerializer() {
		return RecipeSerializer.BANNER_DUPLICATE;
	}
}
