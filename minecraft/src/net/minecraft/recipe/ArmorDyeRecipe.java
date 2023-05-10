package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ArmorDyeRecipe extends SpecialCraftingRecipe {
	public ArmorDyeRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		ItemStack itemStack = ItemStack.EMPTY;
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack2 = recipeInputInventory.getStack(i);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() instanceof DyeableItem) {
					if (!itemStack.isEmpty()) {
						return false;
					}

					itemStack = itemStack2;
				} else {
					if (!(itemStack2.getItem() instanceof DyeItem)) {
						return false;
					}

					list.add(itemStack2);
				}
			}
		}

		return !itemStack.isEmpty() && !list.isEmpty();
	}

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		List<DyeItem> list = Lists.<DyeItem>newArrayList();
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack2 = recipeInputInventory.getStack(i);
			if (!itemStack2.isEmpty()) {
				Item item = itemStack2.getItem();
				if (item instanceof DyeableItem) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2.copy();
				} else {
					if (!(item instanceof DyeItem)) {
						return ItemStack.EMPTY;
					}

					list.add((DyeItem)item);
				}
			}
		}

		return !itemStack.isEmpty() && !list.isEmpty() ? DyeableItem.blendAndSetColor(itemStack, list) : ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.ARMOR_DYE;
	}
}
