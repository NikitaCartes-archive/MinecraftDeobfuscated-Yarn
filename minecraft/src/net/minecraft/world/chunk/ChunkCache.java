package net.minecraft.world.chunk;

import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class ChunkCache implements BlockView, CollisionView {
	protected final int minX;
	protected final int minZ;
	protected final Chunk[][] chunks;
	protected boolean empty;
	protected final World world;

	public ChunkCache(World world, BlockPos minPos, BlockPos maxPos) {
		this.world = world;
		this.minX = ChunkSectionPos.getSectionCoord(minPos.getX());
		this.minZ = ChunkSectionPos.getSectionCoord(minPos.getZ());
		int i = ChunkSectionPos.getSectionCoord(maxPos.getX());
		int j = ChunkSectionPos.getSectionCoord(maxPos.getZ());
		this.chunks = new Chunk[i - this.minX + 1][j - this.minZ + 1];
		ChunkManager chunkManager = world.getChunkManager();
		this.empty = true;

		for (int k = this.minX; k <= i; k++) {
			for (int l = this.minZ; l <= j; l++) {
				this.chunks[k - this.minX][l - this.minZ] = chunkManager.getWorldChunk(k, l);
			}
		}

		for (int k = ChunkSectionPos.getSectionCoord(minPos.getX()); k <= ChunkSectionPos.getSectionCoord(maxPos.getX()); k++) {
			for (int l = ChunkSectionPos.getSectionCoord(minPos.getZ()); l <= ChunkSectionPos.getSectionCoord(maxPos.getZ()); l++) {
				Chunk chunk = this.chunks[k - this.minX][l - this.minZ];
				if (chunk != null && !chunk.areSectionsEmptyBetween(minPos.getY(), maxPos.getY())) {
					this.empty = false;
					return;
				}
			}
		}
	}

	private Chunk getChunk(BlockPos pos) {
		return this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
	}

	private Chunk getChunk(int chunkX, int chunkZ) {
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
	public WorldBorder getWorldBorder() {
		return this.world.getWorldBorder();
	}

	@Override
	public BlockView getChunkAsView(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		Chunk chunk = this.getChunk(pos);
		return chunk.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (this.isOutOfHeightLimit(pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.getBlockState(pos);
		}
	}

	@Override
	public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return Stream.empty();
	}

	@Override
	public Stream<VoxelShape> getCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return this.getBlockCollisions(entity, box);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		if (this.isOutOfHeightLimit(pos)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.getFluidState(pos);
		}
	}

	@Override
	public int getBottomY() {
		return this.world.getBottomY();
	}

	@Override
	public int getHeight() {
		return this.world.getHeight();
	}

	public Profiler getProfiler() {
		return this.world.getProfiler();
	}
}
