package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;

public class FurnaceScreenHandler extends AbstractFurnaceScreenHandler {
	public FurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerType.field_17335, RecipeType.SMELTING, RecipeBookCategory.field_25764, syncId, playerInventory);
	}

	public FurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.field_17335, RecipeType.SMELTING, RecipeBookCategory.field_25764, syncId, playerInventory, inventory, propertyDelegate);
	}
}
