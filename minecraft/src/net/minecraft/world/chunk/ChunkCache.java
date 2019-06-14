package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.Dimension;

public class ChunkCache implements ViewableWorld {
	protected final int minX;
	protected final int minZ;
	protected final Chunk[][] field_9305;
	protected boolean empty;
	protected final World world;

	public ChunkCache(World world, BlockPos blockPos, BlockPos blockPos2) {
		this.world = world;
		this.minX = blockPos.getX() >> 4;
		this.minZ = blockPos.getZ() >> 4;
		int i = blockPos2.getX() >> 4;
		int j = blockPos2.getZ() >> 4;
		this.field_9305 = new Chunk[i - this.minX + 1][j - this.minZ + 1];
		this.empty = true;

		for (int k = this.minX; k <= i; k++) {
			for (int l = this.minZ; l <= j; l++) {
				this.field_9305[k - this.minX][l - this.minZ] = world.method_8402(k, l, ChunkStatus.field_12803, false);
			}
		}

		for (int k = blockPos.getX() >> 4; k <= blockPos2.getX() >> 4; k++) {
			for (int l = blockPos.getZ() >> 4; l <= blockPos2.getZ() >> 4; l++) {
				Chunk chunk = this.field_9305[k - this.minX][l - this.minZ];
				if (chunk != null && !chunk.method_12228(blockPos.getY(), blockPos2.getY())) {
					this.empty = false;
					return;
				}
			}
		}
	}

	@Override
	public int getLightLevel(BlockPos blockPos, int i) {
		return this.world.getLightLevel(blockPos, i);
	}

	@Nullable
	@Override
	public Chunk method_8402(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		int k = i - this.minX;
		int l = j - this.minZ;
		if (k >= 0 && k < this.field_9305.length && l >= 0 && l < this.field_9305[k].length) {
			Chunk chunk = this.field_9305[k][l];
			return (Chunk)(chunk != null ? chunk : new EmptyChunk(this.world, new ChunkPos(i, j)));
		} else {
			return new EmptyChunk(this.world, new ChunkPos(i, j));
		}
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		int k = i - this.minX;
		int l = j - this.minZ;
		return k >= 0 && k < this.field_9305.length && l >= 0 && l < this.field_9305[k].length;
	}

	@Override
	public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
		return this.world.getTopPosition(type, blockPos);
	}

	@Override
	public int getTop(Heightmap.Type type, int i, int j) {
		return this.world.getTop(type, i, j);
	}

	@Override
	public int getAmbientDarkness() {
		return this.world.getAmbientDarkness();
	}

	@Override
	public WorldBorder method_8621() {
		return this.world.method_8621();
	}

	@Override
	public boolean method_8611(@Nullable Entity entity, VoxelShape voxelShape) {
		return true;
	}

	@Override
	public boolean isClient() {
		return false;
	}

	@Override
	public int getSeaLevel() {
		return this.world.getSeaLevel();
	}

	@Override
	public Dimension method_8597() {
		return this.world.method_8597();
	}

	@Nullable
	@Override
	public BlockEntity method_8321(BlockPos blockPos) {
		Chunk chunk = this.method_16955(blockPos);
		return chunk.method_8321(blockPos);
	}

	@Override
	public BlockState method_8320(BlockPos blockPos) {
		if (World.isHeightInvalid(blockPos)) {
			return Blocks.field_10124.method_9564();
		} else {
			Chunk chunk = this.method_16955(blockPos);
			return chunk.method_8320(blockPos);
		}
	}

	@Override
	public FluidState method_8316(BlockPos blockPos) {
		if (World.isHeightInvalid(blockPos)) {
			return Fluids.field_15906.method_15785();
		} else {
			Chunk chunk = this.method_16955(blockPos);
			return chunk.method_8316(blockPos);
		}
	}

	@Override
	public Biome method_8310(BlockPos blockPos) {
		Chunk chunk = this.method_16955(blockPos);
		return chunk.getBiome(blockPos);
	}

	@Override
	public int method_8314(LightType lightType, BlockPos blockPos) {
		return this.world.method_8314(lightType, blockPos);
	}
}
