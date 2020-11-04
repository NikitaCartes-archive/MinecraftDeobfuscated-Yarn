package net.minecraft;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.ChunkPos;

@FunctionalInterface
public interface class_5567 {
	void onChunkStatusChange(ChunkPos chunkPos, ChunkHolder.LevelType levelType);
}
