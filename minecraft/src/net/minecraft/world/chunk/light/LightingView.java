package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;

public interface LightingView {
	default void scheduleChunkLightUpdate(BlockPos blockPos, boolean bl) {
		this.scheduleChunkLightUpdate(blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4, bl);
	}

	void scheduleChunkLightUpdate(int i, int j, int k, boolean bl);
}
