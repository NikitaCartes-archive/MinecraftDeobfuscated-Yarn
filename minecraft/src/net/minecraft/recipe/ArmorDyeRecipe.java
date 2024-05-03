package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;

public class ArmorDyeRecipe extends SpecialCraftingRecipe {
	public ArmorDyeRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		ItemStack itemStack = ItemStack.EMPTY;
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(i);
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

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		List<DyeItem> list = Lists.<DyeItem>newArrayList();
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.isIn(ItemTags.DYEABLE)) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2.copy();
				} else {
					if (!(itemStack2.getItem() instanceof DyeItem dyeItem)) {
						return ItemStack.EMPTY;
					}

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
