package net.minecraft.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;

public class BlastFurnaceContainer extends AbstractFurnaceContainer {
	public BlastFurnaceContainer(int i, PlayerInventory playerInventory) {
		super(ContainerType.BLAST_FURNACE, RecipeType.BLASTING, i, playerInventory);
	}

	public BlastFurnaceContainer(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ContainerType.BLAST_FURNACE, RecipeType.BLASTING, i, playerInventory, inventory, propertyDelegate);
	}
}
