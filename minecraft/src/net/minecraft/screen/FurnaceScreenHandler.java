package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;

public class FurnaceScreenHandler extends AbstractFurnaceScreenHandler {
	public FurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerType.FURNACE, RecipeType.SMELTING, syncId, playerInventory);
	}

	public FurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.FURNACE, RecipeType.SMELTING, syncId, playerInventory, inventory, propertyDelegate);
	}
}
