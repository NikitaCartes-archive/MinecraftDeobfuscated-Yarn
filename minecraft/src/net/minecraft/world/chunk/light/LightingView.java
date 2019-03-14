package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
	default void scheduleChunkLightUpdate(BlockPos blockPos, boolean bl) {
		this.scheduleChunkLightUpdate(ChunkSectionPos.from(blockPos), bl);
	}

	void scheduleChunkLightUpdate(ChunkSectionPos chunkSectionPos, boolean bl);
}
