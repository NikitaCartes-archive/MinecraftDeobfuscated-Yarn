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
import net.minecraft.world.biome.Biomes;

public class ChunkCache implements ExtendedBlockView {
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

	@Nullable
	private Chunk method_18474(BlockPos blockPos) {
		int i = (blockPos.getX() >> 4) - this.minX;
		int j = (blockPos.getZ() >> 4) - this.minZ;
		return i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length ? this.chunks[i][j] : null;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		Chunk chunk = this.method_18474(blockPos);
		return chunk == null ? null : chunk.getBlockEntity(blockPos);
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		if (World.isHeightInvalid(blockPos)) {
			return Blocks.field_10124.getDefaultState();
		} else {
			Chunk chunk = this.method_18474(blockPos);
			return chunk != null ? chunk.getBlockState(blockPos) : Blocks.field_9987.getDefaultState();
		}
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		if (World.isHeightInvalid(blockPos)) {
			return Fluids.field_15906.getDefaultState();
		} else {
			Chunk chunk = this.method_18474(blockPos);
			return chunk != null ? chunk.getFluidState(blockPos) : Fluids.field_15906.getDefaultState();
		}
	}

	@Override
	public Biome getBiome(BlockPos blockPos) {
		Chunk chunk = this.method_18474(blockPos);
		return chunk == null ? Biomes.field_9451 : chunk.getBiome(blockPos);
	}

	@Override
	public int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.world.getLightLevel(lightType, blockPos);
	}
}
