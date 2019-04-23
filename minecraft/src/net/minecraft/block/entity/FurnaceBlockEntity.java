package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.recipe.RecipeType;

public class FurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public FurnaceBlockEntity() {
		super(BlockEntityType.field_11903, RecipeType.SMELTING);
	}

	@Override
	protected Component getContainerName() {
		return new TranslatableComponent("container.furnace");
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new FurnaceContainer(i, playerInventory, this, this.propertyDelegate);
	}
}
