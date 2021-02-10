package net.minecraft.world.chunk;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.ChunkPos;

@FunctionalInterface
public interface ChunkStatusChangeListener {
	void onChunkStatusChange(ChunkPos pos, ChunkHolder.LevelType levelType);
}
