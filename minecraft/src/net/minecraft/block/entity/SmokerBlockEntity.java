package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class SmokerBlockEntity extends FurnaceBlockEntity {
	public SmokerBlockEntity() {
		super(BlockEntityType.SMOKER);
	}

	@Override
	public String getContainerId() {
		return "minecraft:smoker";
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new SmokerContainer(playerInventory, this);
	}
}
