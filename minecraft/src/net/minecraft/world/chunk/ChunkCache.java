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
	protected final Chunk[][] chunks;
	protected boolean empty;
	protected final World world;

	public ChunkCache(World world, BlockPos blockPos, BlockPos blockPos2) {
		this.world = world;
		this.minX = blockPos.getX() >> 4;
		this.minZ = blockPos.getZ() >> 4;
		int i = blockPos2.getX() >> 4;
		int j = blockPos2.getZ() >> 4;
		this.chunks = new Chunk[i - this.minX + 1][j - this.minZ + 1];
		this.empty = true;

		for (int k = this.minX; k <= i; k++) {
			for (int l = this.minZ; l <= j; l++) {
				this.chunks[k - this.minX][l - this.minZ] = world.getChunk(k, l, ChunkStatus.field_12803, false);
			}
		}

		for (int k = blockPos.getX() >> 4; k <= blockPos2.getX() >> 4; k++) {
			for (int l = blockPos.getZ() >> 4; l <= blockPos2.getZ() >> 4; l++) {
				Chunk chunk = this.chunks[k - this.minX][l - this.minZ];
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
	public Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		int k = i - this.minX;
		int l = j - this.minZ;
		return (Chunk)(k >= 0 && k < this.chunks.length && l >= 0 && l < this.chunks[k].length ? this.chunks[k][l] : new EmptyChunk(this.world, new ChunkPos(i, j)));
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		int k = i - this.minX;
		int l = j - this.minZ;
		return k >= 0 && k < this.chunks.length && l >= 0 && l < this.chunks[k].length;
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
	public WorldBorder getWorldBorder() {
		return this.world.getWorldBorder();
	}

	@Override
	public boolean intersectsEntities(@Nullable Entity entity, VoxelShape voxelShape) {
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
	public Dimension getDimension() {
		return this.world.getDimension();
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		Chunk chunk = this.getChunk(blockPos);
		return chunk.getBlockEntity(blockPos);
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		if (World.isHeightInvalid(blockPos)) {
			return Blocks.field_10124.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(blockPos);
			return chunk.getBlockState(blockPos);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		if (World.isHeightInvalid(blockPos)) {
			return Fluids.field_15906.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(blockPos);
			return chunk.getFluidState(blockPos);
		}
	}

	@Override
	public Biome getBiome(BlockPos blockPos) {
		Chunk chunk = this.getChunk(blockPos);
		return chunk.getBiome(blockPos);
	}

	@Override
	public int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.world.getLightLevel(lightType, blockPos);
	}
}
