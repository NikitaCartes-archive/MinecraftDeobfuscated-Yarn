package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SuspiciousStewRecipe extends SpecialCraftingRecipe {
	public SuspiciousStewRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack = recipeInputInventory.getStack(i);
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

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack2 = recipeInputInventory.getStack(i);
			if (!itemStack2.isEmpty()) {
				SuspiciousStewIngredient suspiciousStewIngredient = SuspiciousStewIngredient.of(itemStack2.getItem());
				if (suspiciousStewIngredient != null) {
					SuspiciousStewItem.addEffectToStew(itemStack, suspiciousStewIngredient.getEffectInStew(), suspiciousStewIngredient.getEffectInStewDuration());
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
