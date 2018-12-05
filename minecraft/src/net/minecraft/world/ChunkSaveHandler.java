package net.minecraft.world;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.class_2839;
import net.minecraft.util.MinecraftException;
import net.minecraft.world.chunk.Chunk;

public interface ChunkSaveHandler extends AutoCloseable {
	@Nullable
	class_2839 method_12411(IWorld iWorld, int i, int j);

	void method_12410(World world, Chunk chunk) throws IOException, MinecraftException;

	default boolean method_12412() {
		return false;
	}

	void save();

	default void close() {
	}
}
