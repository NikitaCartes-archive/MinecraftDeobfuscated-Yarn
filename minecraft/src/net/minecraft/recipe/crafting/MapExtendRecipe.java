package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MapExtendRecipe extends ShapedRecipe {
	public MapExtendRecipe(Identifier identifier) {
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
	public boolean matches(Inventory inventory, World world) {
		if (!super.matches(inventory, world)) {
			return false;
		} else {
			ItemStack itemStack = ItemStack.EMPTY;

			for (int i = 0; i < inventory.getInvSize() && itemStack.isEmpty(); i++) {
				ItemStack itemStack2 = inventory.getInvStack(i);
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
				if (mapIcon.method_93() == MapIcon.Direction.field_88 || mapIcon.method_93() == MapIcon.Direction.field_98) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getInvSize() && itemStack.isEmpty(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (itemStack2.getItem() == Items.field_8204) {
				itemStack = itemStack2;
			}
		}

		itemStack = itemStack.copy();
		itemStack.setAmount(1);
		itemStack.getOrCreateTag().putInt("map_scale_direction", 1);
		return itemStack;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializers.MAP_EXTEND;
	}
}
