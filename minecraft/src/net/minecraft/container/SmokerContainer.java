package net.minecraft.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;

public class SmokerContainer extends AbstractFurnaceContainer {
	public SmokerContainer(int syncId, PlayerInventory playerInventory) {
		super(ContainerType.SMOKER, RecipeType.SMOKING, syncId, playerInventory);
	}

	public SmokerContainer(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ContainerType.SMOKER, RecipeType.SMOKING, syncId, playerInventory, inventory, propertyDelegate);
	}
}
