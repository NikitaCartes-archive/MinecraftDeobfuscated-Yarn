package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;

public class BlastFurnaceScreenHandler extends AbstractFurnaceScreenHandler {
	public BlastFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerType.field_17331, RecipeType.BLASTING, RecipeBookCategory.field_25765, syncId, playerInventory);
	}

	public BlastFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.field_17331, RecipeType.BLASTING, RecipeBookCategory.field_25765, syncId, playerInventory, inventory, propertyDelegate);
	}
}
