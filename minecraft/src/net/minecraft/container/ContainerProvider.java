package net.minecraft.container;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

@FunctionalInterface
public interface ContainerProvider {
	@Nullable
	Container createMenu(int syncId, PlayerInventory inv, PlayerEntity player);
}
