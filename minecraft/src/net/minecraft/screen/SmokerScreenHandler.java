package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;

public class SmokerScreenHandler extends AbstractFurnaceScreenHandler {
	public SmokerScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerType.field_17342, RecipeType.SMOKING, RecipeBookCategory.field_25766, syncId, playerInventory);
	}

	public SmokerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.field_17342, RecipeType.SMOKING, RecipeBookCategory.field_25766, syncId, playerInventory, inventory, propertyDelegate);
	}
}
