package net.minecraft.server;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public interface WorldGenerationProgressListener {
	WorldGenerationProgressListener field_23642 = new WorldGenerationProgressListener() {
		@Override
		public void start(ChunkPos spawnPos) {
		}

		@Override
		public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {
		}

		@Override
		public void stop() {
		}
	};

	void start(ChunkPos spawnPos);

	void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status);

	void stop();
}
