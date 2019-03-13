package net.minecraft.recipe.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MapExtendingRecipe extends ShapedRecipe {
	public MapExtendingRecipe(Identifier identifier) {
		super(
			identifier,
			"",
			3,
			3,
			DefaultedList.create(
				Ingredient.EMPTY,
				Ingredient.method_8091(Items.field_8407),
				Ingredient.method_8091(Items.field_8407),
				Ingredient.method_8091(Items.field_8407),
				Ingredient.method_8091(Items.field_8407),
				Ingredient.method_8091(Items.field_8204),
				Ingredient.method_8091(Items.field_8407),
				Ingredient.method_8091(Items.field_8407),
				Ingredient.method_8091(Items.field_8407),
				Ingredient.method_8091(Items.field_8407)
			),
			new ItemStack(Items.field_8895)
		);
	}

	@Override
	public boolean method_17728(CraftingInventory craftingInventory, World world) {
		if (!super.method_17728(craftingInventory, world)) {
			return false;
		} else {
			ItemStack itemStack = ItemStack.EMPTY;

			for (int i = 0; i < craftingInventory.getInvSize() && itemStack.isEmpty(); i++) {
				ItemStack itemStack2 = craftingInventory.method_5438(i);
				if (itemStack2.getItem() == Items.field_8204) {
					itemStack = itemStack2;
				}
			}

			if (itemStack.isEmpty()) {
				return false;
			} else {
				MapState mapState = FilledMapItem.method_8001(itemStack, world);
				if (mapState == null) {
					return false;
				} else {
					return this.method_8120(mapState) ? false : mapState.scale < 4;
				}
			}
		}
	}

	private boolean method_8120(MapState mapState) {
		if (mapState.icons != null) {
			for (MapIcon mapIcon : mapState.icons.values()) {
				if (mapIcon.getType() == MapIcon.Type.field_88 || mapIcon.getType() == MapIcon.Type.field_98) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack method_17727(CraftingInventory craftingInventory) {
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < craftingInventory.getInvSize() && itemStack.isEmpty(); i++) {
			ItemStack itemStack2 = craftingInventory.method_5438(i);
			if (itemStack2.getItem() == Items.field_8204) {
				itemStack = itemStack2;
			}
		}

		itemStack = itemStack.copy();
		itemStack.setAmount(1);
		itemStack.method_7948().putInt("map_scale_direction", 1);
		return itemStack;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	public RecipeSerializer<?> method_8119() {
		return RecipeSerializer.field_9039;
	}
}
