package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class FurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public FurnaceBlockEntity() {
		super(BlockEntityType.FURNACE, RecipeType.SMELTING);
	}

	@Override
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.furnace");
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new FurnaceContainer(i, playerInventory, this, this.propertyDelegate);
	}
}
