package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;

public class SuspiciousStewRecipe extends SpecialCraftingRecipe {
	public SuspiciousStewRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;

		for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.isOf(Blocks.BROWN_MUSHROOM.asItem()) && !bl3) {
					bl3 = true;
				} else if (itemStack.isOf(Blocks.RED_MUSHROOM.asItem()) && !bl2) {
					bl2 = true;
				} else if (itemStack.isIn(ItemTags.SMALL_FLOWERS) && !bl) {
					bl = true;
				} else {
					if (!itemStack.isOf(Items.BOWL) || bl4) {
						return false;
					}

					bl4 = true;
				}
			}
		}

		return bl && bl3 && bl2 && bl4;
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);

		for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack2.isEmpty()) {
				SuspiciousStewIngredient suspiciousStewIngredient = SuspiciousStewIngredient.of(itemStack2.getItem());
				if (suspiciousStewIngredient != null) {
					itemStack.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, suspiciousStewIngredient.getStewEffects());
					break;
				}
			}
		}

		return itemStack;
	}

	@Override
	public boolean fits(int width, int height) {
		return width >= 2 && height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SUSPICIOUS_STEW;
	}
}
