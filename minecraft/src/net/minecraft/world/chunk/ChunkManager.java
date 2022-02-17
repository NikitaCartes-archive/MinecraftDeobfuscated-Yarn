package net.minecraft.world.chunk;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.light.LightingProvider;

public abstract class ChunkManager implements ChunkProvider, AutoCloseable {
	@Nullable
	public WorldChunk getWorldChunk(int chunkX, int chunkZ, boolean create) {
		return (WorldChunk)this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, create);
	}

	@Nullable
	public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
		return this.getWorldChunk(chunkX, chunkZ, false);
	}

	@Nullable
	@Override
	public BlockView getChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
	}

	public boolean isChunkLoaded(int x, int z) {
		return this.getChunk(x, z, ChunkStatus.FULL, false) != null;
	}

	@Nullable
	public abstract Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create);

	public abstract void tick(BooleanSupplier shouldKeepTicking, boolean bl);

	public abstract String getDebugString();

	public abstract int getLoadedChunkCount();

	public void close() throws IOException {
	}

	public abstract LightingProvider getLightingProvider();

	public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
	}

	public void setChunkForced(ChunkPos pos, boolean forced) {
	}
}
