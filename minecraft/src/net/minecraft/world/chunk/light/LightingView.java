package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
	void checkBlock(BlockPos pos);

	void addLightSource(BlockPos pos, int level);

	boolean hasUpdates();

	int doLightUpdates(int i, boolean bl, boolean bl2);

	default void setSectionStatus(BlockPos pos, boolean notReady) {
		this.setSectionStatus(ChunkSectionPos.from(pos), notReady);
	}

	void setSectionStatus(ChunkSectionPos pos, boolean notReady);

	void setColumnEnabled(ChunkPos chunkPos, boolean bl);
}
