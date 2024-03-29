package net.minecraft.recipe;

import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapPostProcessingComponent;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class MapExtendingRecipe extends ShapedRecipe {
	public MapExtendingRecipe(CraftingRecipeCategory category) {
		super(
			"",
			category,
			RawShapedRecipe.create(Map.of('#', Ingredient.ofItems(Items.PAPER), 'x', Ingredient.ofItems(Items.FILLED_MAP)), "###", "#x#", "###"),
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
				} else if (mapState.hasExplorationMapDecoration()) {
					return false;
				} else {
					return mapState.scale < 4;
				}
			}
		}
	}

	@Override
	public ItemStack craft(RecipeInputInventory recipeInputInventory, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = findFilledMap(recipeInputInventory).copyWithCount(1);
		itemStack.set(DataComponentTypes.MAP_POST_PROCESSING, MapPostProcessingComponent.SCALE);
		return itemStack;
	}

	private static ItemStack findFilledMap(RecipeInputInventory inventory) {
		for(int i = 0; i < inventory.size(); ++i) {
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
