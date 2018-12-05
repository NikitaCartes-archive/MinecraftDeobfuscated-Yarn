package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Nameable;

public interface ContainerProvider extends Nameable {
	Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity);

	String getContainerId();
}
