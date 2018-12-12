package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BannerItem;
import net.minecraft.recipe.AbstractRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BannerDuplicateRecipe extends AbstractRecipe {
	public BannerDuplicateRecipe(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			DyeColor dyeColor = null;
			ItemStack itemStack = null;
			ItemStack itemStack2 = null;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				Item item = itemStack3.getItem();
				if (item instanceof BannerItem) {
					BannerItem bannerItem = (BannerItem)item;
					if (dyeColor == null) {
						dyeColor = bannerItem.getColor();
					} else if (dyeColor != bannerItem.getColor()) {
						return false;
					}

					boolean bl = BannerBlockEntity.getPatternCount(itemStack3) > 0;
					if (bl) {
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
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (!itemStack.isEmpty() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setAmount(1);
				return itemStack2;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public DefaultedList<ItemStack> getRemainingStacks(Inventory inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.create(inventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem().hasRecipeRemainder()) {
					defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
				} else if (itemStack.hasTag() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
					ItemStack itemStack2 = itemStack.copy();
					itemStack2.setAmount(1);
					defaultedList.set(i, itemStack2);
				}
			}
		}

		return defaultedList;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializers.BANNER_DUPLICATE;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}
}
