package net.minecraft.screen;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

@FunctionalInterface
public interface ScreenHandlerFactory {
	@Nullable
	ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player);
}
