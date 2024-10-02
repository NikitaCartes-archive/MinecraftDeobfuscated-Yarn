package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookType;

public class FurnaceScreenHandler extends AbstractFurnaceScreenHandler {
	public FurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerType.FURNACE, RecipeType.SMELTING, RecipePropertySet.FURNACE_INPUT, RecipeBookType.FURNACE, syncId, playerInventory);
	}

	public FurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(
			ScreenHandlerType.FURNACE,
			RecipeType.SMELTING,
			RecipePropertySet.FURNACE_INPUT,
			RecipeBookType.FURNACE,
			syncId,
			playerInventory,
			inventory,
			propertyDelegate
		);
	}
}
