package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public interface SpawnLocation {
	boolean isSpawnPositionOk(WorldView world, BlockPos pos, @Nullable EntityType<?> entityType);

	default BlockPos adjustPosition(WorldView world, BlockPos pos) {
		return pos;
	}
}
