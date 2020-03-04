package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;

public class SmokerScreenHandler extends AbstractFurnaceScreenHandler {
	public SmokerScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerType.SMOKER, RecipeType.SMOKING, syncId, playerInventory);
	}

	public SmokerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.SMOKER, RecipeType.SMOKING, syncId, playerInventory, inventory, propertyDelegate);
	}
}
