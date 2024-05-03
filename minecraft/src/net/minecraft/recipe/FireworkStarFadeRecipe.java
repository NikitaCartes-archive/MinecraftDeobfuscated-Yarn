package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class FireworkStarFadeRecipe extends SpecialCraftingRecipe {
	private static final Ingredient INPUT_STAR = Ingredient.ofItems(Items.FIREWORK_STAR);

	public FireworkStarFadeRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		boolean bl = false;
		boolean bl2 = false;

		for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() instanceof DyeItem) {
					bl = true;
				} else {
					if (!INPUT_STAR.test(itemStack)) {
						return false;
					}

					if (bl2) {
						return false;
					}

					bl2 = true;
				}
			}
		}

		return bl2 && bl;
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		IntList intList = new IntArrayList();
		ItemStack itemStack = null;

		for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(i);
			Item item = itemStack2.getItem();
			if (item instanceof DyeItem) {
				intList.add(((DyeItem)item).getColor().getFireworkColor());
			} else if (INPUT_STAR.test(itemStack2)) {
				itemStack = itemStack2.copyWithCount(1);
			}
		}

		if (itemStack != null && !intList.isEmpty()) {
			itemStack.apply(DataComponentTypes.FIREWORK_EXPLOSION, FireworkExplosionComponent.DEFAULT, intList, FireworkExplosionComponent::withFadeColors);
			return itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.FIREWORK_STAR_FADE;
	}
}
