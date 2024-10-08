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
		if (craftingRecipeInput.getStackCount() != 2) {
			return false;
		} else {
			DyeColor dyeColor = null;
			boolean bl = false;
			boolean bl2 = false;

			for (int i = 0; i < craftingRecipeInput.size(); i++) {
				ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
				if (!itemStack.isEmpty()) {
					Item item = itemStack.getItem();
					if (!(item instanceof BannerItem)) {
						return false;
					}

					BannerItem bannerItem = (BannerItem)item;
					if (dyeColor == null) {
						dyeColor = bannerItem.getColor();
					} else if (dyeColor != bannerItem.getColor()) {
						return false;
					}

					int j = itemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT).layers().size();
					if (j > 6) {
						return false;
					}

					if (j > 0) {
						if (bl2) {
							return false;
						}

						bl2 = true;
					} else {
						if (bl) {
							return false;
						}

						bl = true;
					}
				}
			}

			return bl2 && bl;
		}
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
