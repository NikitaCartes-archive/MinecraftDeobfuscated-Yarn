package net.minecraft;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.math.ChunkPos;

public interface class_5571<T> extends AutoCloseable {
	CompletableFuture<class_5566<T>> method_31759(ChunkPos chunkPos);

	void method_31760(class_5566<T> arg);

	void method_31758();

	default void close() throws IOException {
	}
}
