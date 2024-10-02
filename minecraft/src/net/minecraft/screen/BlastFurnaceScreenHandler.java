package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookType;

public class BlastFurnaceScreenHandler extends AbstractFurnaceScreenHandler {
	public BlastFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ScreenHandlerType.BLAST_FURNACE, RecipeType.BLASTING, RecipePropertySet.BLAST_FURNACE_INPUT, RecipeBookType.BLAST_FURNACE, syncId, playerInventory);
	}

	public BlastFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(
			ScreenHandlerType.BLAST_FURNACE,
			RecipeType.BLASTING,
			RecipePropertySet.BLAST_FURNACE_INPUT,
			RecipeBookType.BLAST_FURNACE,
			syncId,
			playerInventory,
			inventory,
			propertyDelegate
		);
	}
}
