package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;

public class BlastFurnaceScreenHandler extends AbstractFurnaceScreenHandler {
	public BlastFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerType.BLAST_FURNACE, RecipeType.BLASTING, syncId, playerInventory);
	}

	public BlastFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.BLAST_FURNACE, RecipeType.BLASTING, syncId, playerInventory, inventory, propertyDelegate);
	}
}
