package net.minecraft.block.entity;

import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class BlastFurnaceBlockEntity extends FurnaceBlockEntity {
	public BlastFurnaceBlockEntity() {
		super(BlockEntityType.BLAST_FURNACE);
	}

	@Override
	public String getContainerId() {
		return "minecraft:blast_furnace";
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new BlastFurnaceContainer(playerInventory, this);
	}
}
