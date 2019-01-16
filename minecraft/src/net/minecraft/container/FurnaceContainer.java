package net.minecraft.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.FurnaceInputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;

public class FurnaceContainer extends AbstractFurnaceContainer {
	public FurnaceContainer(int i, PlayerInventory playerInventory) {
		super(ContainerType.FURNACE, RecipeType.SMELTING, i, playerInventory);
	}

	public FurnaceContainer(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ContainerType.FURNACE, RecipeType.SMELTING, i, playerInventory, inventory, propertyDelegate);
	}

	@Override
	public void method_17697(boolean bl, Recipe<?> recipe, ServerPlayerEntity serverPlayerEntity) {
		new FurnaceInputSlotFiller<>(this).fillInputSlots(serverPlayerEntity, (Recipe<Inventory>)recipe, bl);
	}
}
