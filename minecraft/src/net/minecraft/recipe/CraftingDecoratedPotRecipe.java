package net.minecraft.recipe;

import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;

public class CraftingDecoratedPotRecipe extends SpecialCraftingRecipe {
	public CraftingDecoratedPotRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		if (craftingRecipeInput.getWidth() == 3 && craftingRecipeInput.getHeight() == 3) {
			for (int i = 0; i < craftingRecipeInput.size(); i++) {
				ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
				switch (i) {
					case 1:
					case 3:
					case 5:
					case 7:
						if (!itemStack.isIn(ItemTags.DECORATED_POT_INGREDIENTS)) {
							return false;
						}
						break;
					case 2:
					case 4:
					case 6:
					default:
						if (!itemStack.isOf(Items.AIR)) {
							return false;
						}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		Sherds sherds = new Sherds(
			craftingRecipeInput.getStackInSlot(1).getItem(),
			craftingRecipeInput.getStackInSlot(3).getItem(),
			craftingRecipeInput.getStackInSlot(5).getItem(),
			craftingRecipeInput.getStackInSlot(7).getItem()
		);
		return DecoratedPotBlockEntity.getStackWith(sherds);
	}

	@Override
	public RecipeSerializer<CraftingDecoratedPotRecipe> getSerializer() {
		return RecipeSerializer.CRAFTING_DECORATED_POT;
	}
}
