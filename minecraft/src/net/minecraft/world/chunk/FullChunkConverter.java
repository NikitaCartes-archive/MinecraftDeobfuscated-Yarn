package net.minecraft.world.chunk;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface FullChunkConverter {
	CompletableFuture<Chunk> apply(Chunk chunk);
}
