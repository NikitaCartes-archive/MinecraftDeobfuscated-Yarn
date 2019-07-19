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
import net.minecraft.world.CollisionView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.Dimension;

public class ChunkCache implements CollisionView {
	protected final int minX;
	protected final int minZ;
	protected final Chunk[][] chunks;
	protected boolean empty;
	protected final World world;

	public ChunkCache(World world, BlockPos minPos, BlockPos maxPos) {
		this.world = world;
		this.minX = minPos.getX() >> 4;
		this.minZ = minPos.getZ() >> 4;
		int i = maxPos.getX() >> 4;
		int j = maxPos.getZ() >> 4;
		this.chunks = new Chunk[i - this.minX + 1][j - this.minZ + 1];
		this.empty = true;

		for (int k = this.minX; k <= i; k++) {
			for (int l = this.minZ; l <= j; l++) {
				this.chunks[k - this.minX][l - this.minZ] = world.getChunk(k, l, ChunkStatus.FULL, false);
			}
		}

		for (int k = minPos.getX() >> 4; k <= maxPos.getX() >> 4; k++) {
			for (int l = minPos.getZ() >> 4; l <= maxPos.getZ() >> 4; l++) {
				Chunk chunk = this.chunks[k - this.minX][l - this.minZ];
				if (chunk != null && !chunk.method_12228(minPos.getY(), maxPos.getY())) {
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
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
		int i = chunkX - this.minX;
		int j = chunkZ - this.minZ;
		if (i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length) {
			Chunk chunk = this.chunks[i][j];
			return (Chunk)(chunk != null ? chunk : new EmptyChunk(this.world, new ChunkPos(chunkX, chunkZ)));
		} else {
			return new EmptyChunk(this.world, new ChunkPos(chunkX, chunkZ));
		}
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		int i = chunkX - this.minX;
		int j = chunkZ - this.minZ;
		return i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length;
	}

	@Override
	public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
		return this.world.getTopPosition(type, blockPos);
	}

	@Override
	public int getTop(Heightmap.Type type, int x, int z) {
		return this.world.getTop(type, x, z);
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
	public boolean intersectsEntities(@Nullable Entity except, VoxelShape shape) {
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
	public BlockEntity getBlockEntity(BlockPos pos) {
		Chunk chunk = this.getChunk(pos);
		return chunk.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (World.isHeightInvalid(pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.getBlockState(pos);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		if (World.isHeightInvalid(pos)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.getFluidState(pos);
		}
	}

	@Override
	public Biome getBiome(BlockPos blockPos) {
		Chunk chunk = this.getChunk(blockPos);
		return chunk.getBiome(blockPos);
	}

	@Override
	public int getLightLevel(LightType type, BlockPos pos) {
		return this.world.getLightLevel(type, pos);
	}
}
