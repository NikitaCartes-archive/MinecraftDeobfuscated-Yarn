package net.minecraft.world;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;

public interface ChunkSaveHandler extends AutoCloseable {
	@Nullable
	ProtoChunk readChunk(IWorld iWorld, int i, int j);

	void saveChunk(World world, Chunk chunk) throws IOException, SessionLockException;

	default boolean saveNextChunk() {
		return false;
	}

	void saveAllChunks();

	default void close() {
	}
}
