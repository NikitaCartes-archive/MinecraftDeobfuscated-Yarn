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
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class ChunkManager implements ChunkProvider, AutoCloseable {
	@Nullable
	public WorldChunk method_12126(int i, int j, boolean bl) {
		return (WorldChunk)this.method_12121(i, j, ChunkStatus.field_12803, bl);
	}

	@Nullable
	@Override
	public BlockView getChunk(int i, int j) {
		return this.method_12121(i, j, ChunkStatus.field_12798, false);
	}

	public boolean isChunkLoaded(int i, int j) {
		return this.method_12121(i, j, ChunkStatus.field_12803, false) != null;
	}

	@Nullable
	public abstract Chunk method_12121(int i, int j, ChunkStatus chunkStatus, boolean bl);

	@Environment(EnvType.CLIENT)
	public abstract void tick(BooleanSupplier booleanSupplier);

	public abstract String getStatus();

	public abstract ChunkGenerator<?> getChunkGenerator();

	public void close() throws IOException {
	}

	public abstract LightingProvider method_12130();

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
