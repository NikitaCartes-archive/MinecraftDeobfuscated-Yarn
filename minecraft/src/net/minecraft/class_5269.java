package net.minecraft;

import net.minecraft.util.math.BlockPos;

public interface class_5269 extends class_5217 {
	void setSpawnX(int spawnX);

	void setSpawnY(int spawnY);

	void setSpawnZ(int spawnZ);

	default void setSpawnPos(BlockPos pos) {
		this.setSpawnX(pos.getX());
		this.setSpawnY(pos.getY());
		this.setSpawnZ(pos.getZ());
	}

	void setTime(long time);

	void setTimeOfDay(long timeOfDay);
}
