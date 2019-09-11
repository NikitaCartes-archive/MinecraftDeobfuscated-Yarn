package net.minecraft.world.chunk;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.light.LightingProvider;

public abstract class ChunkManager implements ChunkProvider, AutoCloseable {
	@Nullable
	public WorldChunk getWorldChunk(int i, int j, boolean bl) {
		return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL, bl);
	}

	@Nullable
	public WorldChunk getWorldChunk(int i, int j) {
		return this.getWorldChunk(i, j, false);
	}

	@Nullable
	@Override
	public BlockView getChunk(int i, int j) {
		return this.getChunk(i, j, ChunkStatus.EMPTY, false);
	}

	public boolean isChunkLoaded(int i, int j) {
		return this.getChunk(i, j, ChunkStatus.FULL, false) != null;
	}

	@Nullable
	public abstract Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl);

	@Environment(EnvType.CLIENT)
	public abstract void tick(BooleanSupplier booleanSupplier);

	public abstract String getDebugString();

	public void close() throws IOException {
	}

	public abstract LightingProvider getLightingProvider();

	public void setMobSpawnOptions(boolean bl, boolean bl2) {
	}

	public void setChunkForced(ChunkPos chunkPos, boolean bl) {
	}

	public boolean shouldTickEntity(Entity entity) {
		return true;
	}

	public boolean shouldTickChunk(ChunkPos chunkPos) {
		return true;
	}

	public boolean shouldTickBlock(BlockPos blockPos) {
		return true;
	}
}
