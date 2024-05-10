package net.minecraft;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public interface class_9760 {
	class_9761 method_60448(long l);

	void method_60441(class_9761 arg);

	CompletableFuture<Chunk> method_60442(class_9761 arg, class_9770 arg2, class_9762<class_9761> arg3);

	class_9759 method_60443(ChunkStatus chunkStatus, ChunkPos chunkPos);

	void method_60450();
}
