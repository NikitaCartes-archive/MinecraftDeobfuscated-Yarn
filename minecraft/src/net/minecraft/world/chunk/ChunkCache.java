package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ChunkCache implements ExtendedBlockView {
	protected int minX;
	protected int minZ;
	protected WorldChunk[][] chunks;
	protected boolean field_9302;
	protected World world;

	public ChunkCache(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
		this.world = world;
		this.minX = blockPos.getX() - i >> 4;
		this.minZ = blockPos.getZ() - i >> 4;
		int j = blockPos2.getX() + i >> 4;
		int k = blockPos2.getZ() + i >> 4;
		this.chunks = new WorldChunk[j - this.minX + 1][k - this.minZ + 1];
		this.field_9302 = true;

		for (int l = this.minX; l <= j; l++) {
			for (int m = this.minZ; m <= k; m++) {
				this.chunks[l - this.minX][m - this.minZ] = world.getChunk(l, m);
			}
		}

		for (int l = blockPos.getX() >> 4; l <= blockPos2.getX() >> 4; l++) {
			for (int m = blockPos.getZ() >> 4; m <= blockPos2.getZ() >> 4; m++) {
				WorldChunk worldChunk = this.chunks[l - this.minX][m - this.minZ];
				if (worldChunk != null && !worldChunk.method_12228(blockPos.getY(), blockPos2.getY())) {
					this.field_9302 = false;
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		return this.getBlockEntity(blockPos, WorldChunk.AccessType.CREATE);
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos blockPos, WorldChunk.AccessType accessType) {
		int i = (blockPos.getX() >> 4) - this.minX;
		int j = (blockPos.getZ() >> 4) - this.minZ;
		return this.chunks[i][j].getBlockEntity(blockPos, accessType);
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		if (!World.isHeightInvaid(blockPos)) {
			int i = (blockPos.getX() >> 4) - this.minX;
			int j = (blockPos.getZ() >> 4) - this.minZ;
			if (i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length) {
				WorldChunk worldChunk = this.chunks[i][j];
				if (worldChunk != null) {
					return worldChunk.getBlockState(blockPos);
				}
			}
		}

		return Blocks.field_10124.getDefaultState();
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
			int i = (blockPos.getX() >> 4) - this.minX;
			int j = (blockPos.getZ() >> 4) - this.minZ;
			if (i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length) {
				WorldChunk worldChunk = this.chunks[i][j];
				if (worldChunk != null) {
					return worldChunk.getFluidState(blockPos);
				}
			}
		}

		return Fluids.field_15906.getDefaultState();
	}

	@Override
	public Biome getBiome(BlockPos blockPos) {
		int i = (blockPos.getX() >> 4) - this.minX;
		int j = (blockPos.getZ() >> 4) - this.minZ;
		return this.chunks[i][j].getBiome(blockPos);
	}

	@Override
	public int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.world.getLightLevel(lightType, blockPos);
	}
}
