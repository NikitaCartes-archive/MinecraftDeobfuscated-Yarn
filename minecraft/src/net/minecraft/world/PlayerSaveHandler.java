package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

public interface PlayerSaveHandler {
	void savePlayerData(PlayerEntity player);

	@Nullable
	CompoundTag loadPlayerData(PlayerEntity player);
}
