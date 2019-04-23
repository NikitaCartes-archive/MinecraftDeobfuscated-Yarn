package net.minecraft.server;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public interface WorldGenerationProgressListener {
	void start(ChunkPos chunkPos);

	void setChunkStatus(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus);

	void stop();
}
