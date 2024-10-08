package net.minecraft.recipe;

import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;

public class CraftingDecoratedPotRecipe extends SpecialCraftingRecipe {
	public CraftingDecoratedPotRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	private static ItemStack getBack(CraftingRecipeInput input) {
		return input.getStackInSlot(1, 0);
	}

	private static ItemStack getLeft(CraftingRecipeInput input) {
		return input.getStackInSlot(0, 1);
	}

	private static ItemStack getRight(CraftingRecipeInput input) {
		return input.getStackInSlot(2, 1);
	}

	private static ItemStack getFront(CraftingRecipeInput input) {
		return input.getStackInSlot(1, 2);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		return craftingRecipeInput.getWidth() == 3 && craftingRecipeInput.getHeight() == 3 && craftingRecipeInput.getStackCount() == 4
			? getBack(craftingRecipeInput).isIn(ItemTags.DECORATED_POT_INGREDIENTS)
				&& getLeft(craftingRecipeInput).isIn(ItemTags.DECORATED_POT_INGREDIENTS)
				&& getRight(craftingRecipeInput).isIn(ItemTags.DECORATED_POT_INGREDIENTS)
				&& getFront(craftingRecipeInput).isIn(ItemTags.DECORATED_POT_INGREDIENTS)
			: false;
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		Sherds sherds = new Sherds(
			getBack(craftingRecipeInput).getItem(),
			getLeft(craftingRecipeInput).getItem(),
			getRight(craftingRecipeInput).getItem(),
			getFront(craftingRecipeInput).getItem()
		);
		return DecoratedPotBlockEntity.getStackWith(sherds);
	}

	@Override
	public RecipeSerializer<CraftingDecoratedPotRecipe> getSerializer() {
		return RecipeSerializer.CRAFTING_DECORATED_POT;
	}
}
