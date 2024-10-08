package net.minecraft.recipe;

import java.util.ArrayList;
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
		if (craftingRecipeInput.getStackCount() < 2) {
			return false;
		} else {
			boolean bl = false;
			boolean bl2 = false;

			for (int i = 0; i < craftingRecipeInput.size(); i++) {
				ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
				if (!itemStack.isEmpty()) {
					if (itemStack.isIn(ItemTags.DYEABLE)) {
						if (bl) {
							return false;
						}

						bl = true;
					} else {
						if (!(itemStack.getItem() instanceof DyeItem)) {
							return false;
						}

						bl2 = true;
					}
				}
			}

			return bl2 && bl;
		}
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		List<DyeItem> list = new ArrayList();
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < craftingRecipeInput.size(); i++) {
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
	public RecipeSerializer<ArmorDyeRecipe> getSerializer() {
		return RecipeSerializer.ARMOR_DYE;
	}
}
