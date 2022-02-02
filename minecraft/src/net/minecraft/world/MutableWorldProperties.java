package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public interface MutableWorldProperties extends WorldProperties {
	void setSpawnX(int spawnX);

	void setSpawnY(int spawnY);

	void setSpawnZ(int spawnZ);

	void setSpawnAngle(float spawnAngle);

	default void setSpawnPos(BlockPos pos, float angle) {
		this.setSpawnX(pos.getX());
		this.setSpawnY(pos.getY());
		this.setSpawnZ(pos.getZ());
		this.setSpawnAngle(angle);
	}
}
