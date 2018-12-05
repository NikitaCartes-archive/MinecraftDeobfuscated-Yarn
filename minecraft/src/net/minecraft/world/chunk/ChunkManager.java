package net.minecraft.world.chunk;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class ChunkManager implements ChunkView, AutoCloseable {
	@Nullable
	public WorldChunk method_12126(int i, int j, boolean bl) {
		return (WorldChunk)this.getChunkSync(i, j, ChunkStatus.field_12803, bl);
	}

	@Nullable
	@Override
	public BlockView get(int i, int j) {
		return this.getChunkSync(i, j, ChunkStatus.field_12798, false);
	}

	public boolean method_12123(int i, int j) {
		return this.getChunkSync(i, j, ChunkStatus.field_12803, false) != null;
	}

	@Nullable
	public abstract Chunk getChunkSync(int i, int j, ChunkStatus chunkStatus, boolean bl);

	@Environment(EnvType.CLIENT)
	public abstract void tick(BooleanSupplier booleanSupplier);

	public abstract String getStatus();

	public abstract ChunkGenerator<?> getChunkGenerator();

	public void close() {
	}

	public abstract LightingProvider getLightingProvider();

	public void setMobSpawnOptions(boolean bl, boolean bl2) {
	}

	public void method_12124(ChunkPos chunkPos, boolean bl) {
	}

	public boolean method_12125(Entity entity) {
		return true;
	}
}
