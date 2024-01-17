package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public interface MutableWorldProperties extends WorldProperties {
	void setSpawnPos(BlockPos pos, float angle);
}
