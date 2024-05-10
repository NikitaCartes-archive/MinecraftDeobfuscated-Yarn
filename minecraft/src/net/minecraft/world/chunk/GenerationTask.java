package net.minecraft.world.chunk;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_9761;
import net.minecraft.class_9762;
import net.minecraft.class_9770;

/**
 * A task called when a chunk needs to be generated.
 */
@FunctionalInterface
public interface GenerationTask {
	CompletableFuture<Chunk> doWork(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk);
}
