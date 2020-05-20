package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public interface MutableWorldProperties extends WorldProperties {
	void setSpawnX(int spawnX);

	void setSpawnY(int spawnY);

	void setSpawnZ(int spawnZ);

	default void setSpawnPos(BlockPos pos) {
		this.setSpawnX(pos.getX());
		this.setSpawnY(pos.getY());
		this.setSpawnZ(pos.getZ());
	}
}
