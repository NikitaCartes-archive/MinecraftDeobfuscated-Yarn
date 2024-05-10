package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.input.CraftingRecipeInput;

/**
 * Represents an inventory that is an input for a recipe, such as
 * crafting table inputs.
 */
public interface RecipeInputInventory extends Inventory, RecipeInputProvider {
	/**
	 * {@return the width of the recipe grid}
	 */
	int getWidth();

	/**
	 * {@return the height of the recipe grid}
	 */
	int getHeight();

	/**
	 * {@return the stacks held by the inventory}
	 */
	List<ItemStack> getHeldStacks();

	default CraftingRecipeInput createRecipeInput() {
		return this.createPositionedRecipeInput().input();
	}

	default CraftingRecipeInput.Positioned createPositionedRecipeInput() {
		return CraftingRecipeInput.createPositioned(this.getWidth(), this.getHeight(), this.getHeldStacks());
	}
}
