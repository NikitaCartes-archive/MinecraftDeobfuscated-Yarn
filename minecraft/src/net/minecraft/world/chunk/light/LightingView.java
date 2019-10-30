package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
	default void updateSectionStatus(BlockPos pos, boolean status) {
		this.updateSectionStatus(ChunkSectionPos.from(pos), status);
	}

	void updateSectionStatus(ChunkSectionPos pos, boolean status);
}
