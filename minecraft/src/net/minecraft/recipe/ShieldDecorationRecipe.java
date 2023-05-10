package net.minecraft.recipe;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShieldDecorationRecipe extends SpecialCraftingRecipe {
	public ShieldDecorationRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		ItemStack itemStack = ItemStack.EMPTY;
		ItemStack itemStack2 = ItemStack.EMPTY;

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack3 = recipeInputInventory.getStack(i);
			if (!itemStack3.isEmpty()) {
				if (itemStack3.getItem() instanceof BannerItem) {
					if (!itemStack2.isEmpty()) {
						return false;
					}

					itemStack2 = itemStack3;
				} else {
					if (!itemStack3.isOf(Items.SHIELD)) {
						return false;
					}

					if (!itemStack.isEmpty()) {
						return false;
					}

					if (BlockItem.getBlockEntityNbt(itemStack3) != null) {
						return false;
					}

					itemStack = itemStack3;
				}
			}
		}

		return !itemStack.isEmpty() && !itemStack2.isEmpty();
	}

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		ItemStack itemStack = ItemStack.EMPTY;
		ItemStack itemStack2 = ItemStack.EMPTY;

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack3 = recipeInputInventory.getStack(i);
			if (!itemStack3.isEmpty()) {
				if (itemStack3.getItem() instanceof BannerItem) {
					itemStack = itemStack3;
				} else if (itemStack3.isOf(Items.SHIELD)) {
					itemStack2 = itemStack3.copy();
				}
			}
		}

		if (itemStack2.isEmpty()) {
			return itemStack2;
		} else {
			NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
			NbtCompound nbtCompound2 = nbtCompound == null ? new NbtCompound() : nbtCompound.copy();
			nbtCompound2.putInt("Base", ((BannerItem)itemStack.getItem()).getColor().getId());
			BlockItem.setBlockEntityNbt(itemStack2, BlockEntityType.BANNER, nbtCompound2);
			return itemStack2;
		}
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SHIELD_DECORATION;
	}
}
