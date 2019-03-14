package net.minecraft.block.entity;

import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class BlastFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public BlastFurnaceBlockEntity() {
		super(BlockEntityType.BLAST_FURNACE, RecipeType.BLASTING);
	}

	@Override
	protected TextComponent getContainerName() {
		return new TranslatableTextComponent("container.blast_furnace");
	}

	@Override
	protected int getFuelTime(ItemStack itemStack) {
		return super.getFuelTime(itemStack) / 2;
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new BlastFurnaceContainer(i, playerInventory, this, this.propertyDelegate);
	}
}
