package net.minecraft.server;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public interface WorldGenerationProgressListener {
	void start(ChunkPos spawnPos);

	void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status);

	void stop();
}
