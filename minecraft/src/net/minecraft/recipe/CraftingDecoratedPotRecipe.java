package net.minecraft.recipe;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CraftingDecoratedPotRecipe extends SpecialCraftingRecipe {
	public CraftingDecoratedPotRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		if (!this.fits(recipeInputInventory.getWidth(), recipeInputInventory.getHeight())) {
			return false;
		} else {
			for (int i = 0; i < recipeInputInventory.size(); i++) {
				ItemStack itemStack = recipeInputInventory.getStack(i);
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
		}
	}

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		DecoratedPotBlockEntity.Sherds sherds = new DecoratedPotBlockEntity.Sherds(
			recipeInputInventory.getStack(1).getItem(),
			recipeInputInventory.getStack(3).getItem(),
			recipeInputInventory.getStack(5).getItem(),
			recipeInputInventory.getStack(7).getItem()
		);
		return getPotStackWith(sherds);
	}

	public static ItemStack getPotStackWith(DecoratedPotBlockEntity.Sherds sherds) {
		ItemStack itemStack = Items.DECORATED_POT.getDefaultStack();
		NbtCompound nbtCompound = sherds.toNbt(new NbtCompound());
		BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.DECORATED_POT, nbtCompound);
		return itemStack;
	}

	@Override
	public boolean fits(int width, int height) {
		return width == 3 && height == 3;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.CRAFTING_DECORATED_POT;
	}
}
