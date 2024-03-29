package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;

public class ArmorDyeRecipe extends SpecialCraftingRecipe {
	public ArmorDyeRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		ItemStack itemStack = ItemStack.EMPTY;
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for(int i = 0; i < recipeInputInventory.size(); ++i) {
			ItemStack itemStack2 = recipeInputInventory.getStack(i);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.isIn(ItemTags.DYEABLE)) {
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

	public ItemStack craft(RecipeInputInventory recipeInputInventory, RegistryWrapper.WrapperLookup wrapperLookup) {
		List<DyeItem> list = Lists.<DyeItem>newArrayList();
		ItemStack itemStack = ItemStack.EMPTY;

		for(int i = 0; i < recipeInputInventory.size(); ++i) {
			ItemStack itemStack2 = recipeInputInventory.getStack(i);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.isIn(ItemTags.DYEABLE)) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2.copy();
				} else {
					Item var8 = itemStack2.getItem();
					if (!(var8 instanceof DyeItem)) {
						return ItemStack.EMPTY;
					}

					DyeItem dyeItem = (DyeItem)var8;
					list.add(dyeItem);
				}
			}
		}

		return !itemStack.isEmpty() && !list.isEmpty() ? DyedColorComponent.setColor(itemStack, list) : ItemStack.EMPTY;
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
