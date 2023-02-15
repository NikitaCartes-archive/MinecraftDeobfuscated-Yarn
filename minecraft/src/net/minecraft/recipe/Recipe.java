package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

/**
 * A recipe is an arrangement of item stacks in an inventory that can
 * yield a product item stack.
 * 
 * <p>Recipes are loaded by and stored in the {@link RecipeManager}. They
 * are part of the server's data packs and are sent to the client, governed
 * by their {@linkplain #getSerializer() serializers}. Hence, recipes
 * should not be stored, as they may become obsolete after reloads.
 * 
 * <p>A few of the methods in this class are dedicated to crafting recipes
 * or recipe books. Users can have stub implementations if they do not use
 * those functionalities.
 */
public interface Recipe<C extends Inventory> {
	/**
	 * {@return whether this recipe matches the contents inside the
	 * {@code inventory} in the given {@code world}}
	 * 
	 * <p>The {@code world} currently is only used by the map cloning recipe to
	 * prevent duplication of explorer maps.
	 * 
	 * @param inventory the input inventory
	 * @param world the input world
	 */
	boolean matches(C inventory, World world);

	/**
	 * Crafts this recipe.
	 * 
	 * <p>This method does not perform side effects on the {@code inventory}.
	 * 
	 * <p>This method should return a new item stack on each call.
	 * 
	 * @return the resulting item stack
	 * 
	 * @param inventory the input inventory
	 */
	ItemStack craft(C inventory, DynamicRegistryManager registryManager);

	/**
	 * {@return whether this recipe will fit into the given grid size}
	 * 
	 * <p>This is currently only used by recipe book.
	 * 
	 * @param width the width of the input inventory
	 * @param height the height of the input inventory
	 */
	boolean fits(int width, int height);

	/**
	 * {@return a preview of the recipe's output}
	 * 
	 * <p>The returned stack should not be modified. To obtain the actual output,
	 * call {@link #craft(Inventory, DynamicRegistryManager)}.
	 */
	ItemStack getOutput(DynamicRegistryManager registryManager);

	/**
	 * {@return the remaining stacks to be left in the {@code inventory} after the recipe is used}
	 * At each index, the remainder item stack in the list should correspond to the original
	 * item stack in the {@code inventory}.
	 * 
	 * @implSpec The default implementation returns a list of the same size as the {@code inventory}.
	 * At each index, the list contains the {@linkplain net.minecraft.item.Item#getRecipeRemainder()
	 * remainder} of the item stack at the same index in the {@code inventory}, or is {@linkplain
	 * ItemStack#EMPTY empty} if the stack has no remainder.
	 * 
	 * @param inventory the input inventory
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
	 * {@return the ingredients accepted as inputs for this recipe} Used by the recipe book
	 * when displaying a ghost form of this recipe on the crafting grid as well as for
	 * previewing the possible inputs in the book itself.
	 */
	default DefaultedList<Ingredient> getIngredients() {
		return DefaultedList.of();
	}

	/**
	 * {@return whether this recipe is ignored by the recipe book} If a recipe
	 * is ignored by the recipe book, it will be never displayed. In addition,
	 * it won't be restricted by the {@link net.minecraft.world.GameRules#DO_LIMITED_CRAFTING
	 * doLimitedCrafting} game rule.
	 */
	default boolean isIgnoredInRecipeBook() {
		return false;
	}

	default boolean showNotification() {
		return true;
	}

	/**
	 * {@return a group this recipe belongs in, or an empty string} This is
	 * only used by the recipe book.
	 * 
	 * <p>The group string is arbitrary, and is not rendered anywhere; in
	 * the recipe book, recipes with the same group will belong to the same
	 * cell in the grid of recipes. If the string is empty, this recipe will
	 * belong to its own cell.
	 */
	default String getGroup() {
		return "";
	}

	/**
	 * {@return an item rendered on the top left of the {@linkplain #getOutput(DynamicRegistryManager)
	 * output preview} on the recipe toast when a new recipe is unlocked} This
	 * can be interpreted as a catalyst for the recipe.
	 */
	default ItemStack createIcon() {
		return new ItemStack(Blocks.CRAFTING_TABLE);
	}

	/**
	 * {@return the ID of this recipe}
	 */
	Identifier getId();

	/**
	 * {@return the serializer associated with this recipe}
	 */
	RecipeSerializer<?> getSerializer();

	/**
	 * {@return the type of this recipe}
	 * 
	 * <p>The {@code type} in the recipe JSON format is the {@linkplain
	 * #getSerializer() serializer} instead.
	 */
	RecipeType<?> getType();

	/**
	 * {@return whether this recipe has no ingredient or has any empty ingredient}
	 * The recipe book uses this to ignore recipes for display.
	 */
	default boolean isEmpty() {
		DefaultedList<Ingredient> defaultedList = this.getIngredients();
		return defaultedList.isEmpty() || defaultedList.stream().anyMatch(ingredient -> ingredient.getMatchingStacks().length == 0);
	}
}
