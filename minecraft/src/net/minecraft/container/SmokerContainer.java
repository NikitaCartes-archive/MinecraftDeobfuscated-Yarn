package net.minecraft.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;

public class SmokerContainer extends AbstractFurnaceContainer {
	public SmokerContainer(int i, PlayerInventory playerInventory) {
		super(ContainerType.field_17342, RecipeType.SMOKING, i, playerInventory);
	}

	public SmokerContainer(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ContainerType.field_17342, RecipeType.SMOKING, i, playerInventory, inventory, propertyDelegate);
	}
}
