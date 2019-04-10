package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
	default void updateSectionStatus(BlockPos blockPos, boolean bl) {
		this.updateSectionStatus(ChunkSectionPos.from(blockPos), bl);
	}

	void updateSectionStatus(ChunkSectionPos chunkSectionPos, boolean bl);
}
