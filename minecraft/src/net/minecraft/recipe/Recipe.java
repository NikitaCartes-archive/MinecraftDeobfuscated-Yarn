package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public interface Recipe<C extends Inventory> {
	/**
	 * Determines whether this recipe matches the contents currently placed inside the available crafting grid.
	 */
	boolean matches(C inventory, World world);

	/**
	 * Crafts this recipe.
	 * 
	 * <p>This method may perform side effects on the {@code inventory} argument.</p>
	 * 
	 * <p>This method should return a new item stack on each call.</p>
	 * 
	 * @return the resulting item stack
	 */
	ItemStack craft(C inventory);

	/**
	 * Determines whether this recipe's pattern will fit into the available crafting area.
	 */
	boolean fits(int width, int height);

	ItemStack getOutput();

	/**
	 * Returns the remaining stacks to be left in the crafting grid after crafting is complete.
	 * Should return the same number of items as the input grid contains in the same order they're expected
	 * to appear in that grid.
	 * 
	 * @implSpec Default implementation simply returns a grid of all empty stacks where all stacks from the
	 * input grid have been replaced with the result of calling {@link Item#getRecipeRemainder()} on them.
	 */
	default DefaultedList<ItemStack> getRemainder(C inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			Item item = inventory.getStack(i).getItem();
			if (item.hasRecipeRemainder()) {
				defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
			}
		}

		return defaultedList;
	}

	/**
	 * The ingredients accepted as inputs for this recipe. Used by the recipe book
	 * when displaying a ghost form of this recipe on the crafting grid as well as for
	 * previewing the possible inputs in the book itself.
	 */
	default DefaultedList<Ingredient> getIngredients() {
		return DefaultedList.of();
	}

	default boolean isIgnoredInRecipeBook() {
		return false;
	}

	/**
	 * Optional group this recipe belongs in. Used to group recipes into different categories by the recipe book.
	 */
	default String getGroup() {
		return "";
	}

	/**
	 * Creates the stack that is displayed on the recipe book tab containing this recipe, and on a toast when a recipe of this type is unlocked.
	 * Used in conjunction with {@link #getGroup()}.
	 */
	default ItemStack createIcon() {
		return new ItemStack(Blocks.CRAFTING_TABLE);
	}

	Identifier getId();

	RecipeSerializer<?> getSerializer();

	RecipeType<?> getType();

	default boolean isEmpty() {
		DefaultedList<Ingredient> defaultedList = this.getIngredients();
		return defaultedList.isEmpty() || defaultedList.stream().anyMatch(ingredient -> ingredient.getMatchingStacksClient().length == 0);
	}
}
