package net.minecraft.recipe;

import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapPostProcessingComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class MapExtendingRecipe extends ShapedRecipe {
	public MapExtendingRecipe(CraftingRecipeCategory category) {
		super(
			"",
			category,
			RawShapedRecipe.create(Map.of('#', Ingredient.ofItem(Items.PAPER), 'x', Ingredient.ofItem(Items.FILLED_MAP)), "###", "#x#", "###"),
			new ItemStack(Items.MAP)
		);
	}

	@Override
	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		if (!super.matches(craftingRecipeInput, world)) {
			return false;
		} else {
			ItemStack itemStack = findFilledMap(craftingRecipeInput);
			if (itemStack.isEmpty()) {
				return false;
			} else {
				MapState mapState = FilledMapItem.getMapState(itemStack, world);
				if (mapState == null) {
					return false;
				} else {
					return mapState.hasExplorationMapDecoration() ? false : mapState.scale < 4;
				}
			}
		}
	}

	@Override
	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = findFilledMap(craftingRecipeInput).copyWithCount(1);
		itemStack.set(DataComponentTypes.MAP_POST_PROCESSING, MapPostProcessingComponent.SCALE);
		return itemStack;
	}

	private static ItemStack findFilledMap(CraftingRecipeInput input) {
		for (int i = 0; i < input.size(); i++) {
			ItemStack itemStack = input.getStackInSlot(i);
			if (itemStack.contains(DataComponentTypes.MAP_ID)) {
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
	public RecipeSerializer<MapExtendingRecipe> getSerializer() {
		return RecipeSerializer.MAP_EXTENDING;
	}
}
