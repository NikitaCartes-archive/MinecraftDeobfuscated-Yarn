package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class FurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public FurnaceBlockEntity() {
		super(BlockEntityType.field_11903, RecipeType.SMELTING);
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.furnace");
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new FurnaceContainer(i, playerInventory, this, this.propertyDelegate);
	}
}
