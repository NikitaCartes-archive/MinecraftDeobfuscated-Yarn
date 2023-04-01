package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DupeHackRecipe extends SpecialCraftingRecipe {
	public DupeHackRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		boolean bl = false;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack2 = craftingInventory.getStack(i);
			if (!itemStack2.isEmpty()) {
				if (!bl && itemStack2.isOf(Items.DUPE_HACK)) {
					bl = true;
				} else {
					if (!itemStack.isEmpty()) {
						return false;
					}

					itemStack = itemStack2;
				}
			}
		}

		return !itemStack.isEmpty() && bl;
	}

	public ItemStack craft(CraftingInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
		boolean bl = false;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack2 = craftingInventory.getStack(i);
			if (!itemStack2.isEmpty()) {
				if (!bl && itemStack2.isOf(Items.DUPE_HACK)) {
					bl = true;
				} else {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2;
				}
			}
		}

		if (!itemStack.isEmpty() && bl) {
			ItemStack itemStack3 = itemStack.copy();
			itemStack3.setCount(2);
			return itemStack3;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean fits(int width, int height) {
		return width >= 2 && height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.CRAFTING_SPECIAL_DUPEHACK;
	}
}
