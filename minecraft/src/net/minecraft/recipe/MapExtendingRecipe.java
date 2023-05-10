package net.minecraft.recipe;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class MapExtendingRecipe extends ShapedRecipe {
	public MapExtendingRecipe(Identifier id, CraftingRecipeCategory category) {
		super(
			id,
			"",
			category,
			3,
			3,
			DefaultedList.copyOf(
				Ingredient.EMPTY,
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.FILLED_MAP),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER)
			),
			new ItemStack(Items.MAP)
		);
	}

	@Override
	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		if (!super.matches(recipeInputInventory, world)) {
			return false;
		} else {
			ItemStack itemStack = findFilledMap(recipeInputInventory);
			if (itemStack.isEmpty()) {
				return false;
			} else {
				MapState mapState = FilledMapItem.getMapState(itemStack, world);
				if (mapState == null) {
					return false;
				} else {
					return mapState.hasMonumentIcon() ? false : mapState.scale < 4;
				}
			}
		}
	}

	@Override
	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		ItemStack itemStack = findFilledMap(recipeInputInventory).copyWithCount(1);
		itemStack.getOrCreateNbt().putInt("map_scale_direction", 1);
		return itemStack;
	}

	private static ItemStack findFilledMap(RecipeInputInventory inventory) {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack itemStack = inventory.getStack(i);
			if (itemStack.isOf(Items.FILLED_MAP)) {
				return itemStack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.MAP_EXTENDING;
	}
}
