package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
	default void method_15552(BlockPos blockPos, boolean bl) {
		this.method_15551(ChunkSectionPos.from(blockPos), bl);
	}

	void method_15551(ChunkSectionPos chunkSectionPos, boolean bl);
}
