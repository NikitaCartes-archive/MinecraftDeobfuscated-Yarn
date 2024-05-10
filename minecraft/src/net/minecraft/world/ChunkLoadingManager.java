package net.minecraft.world;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkLoader;
import net.minecraft.world.chunk.ChunkStatus;

public interface ChunkLoadingManager {
	AbstractChunkHolder acquire(long pos);

	void release(AbstractChunkHolder chunkHolder);

	CompletableFuture<Chunk> generate(AbstractChunkHolder chunkHolder, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks);

	ChunkLoader createLoader(ChunkStatus requestedStatus, ChunkPos pos);

	void updateChunks();
}
