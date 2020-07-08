package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
	default void setSectionStatus(BlockPos pos, boolean notReady) {
		this.setSectionStatus(ChunkSectionPos.from(pos), notReady);
	}

	void setSectionStatus(ChunkSectionPos pos, boolean notReady);
}
