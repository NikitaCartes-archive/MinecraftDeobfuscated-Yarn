package net.minecraft.client.render.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class ChunkRendererRegion implements ExtendedBlockView {
	protected final int chunkXOffset;
	protected final int chunkZOffset;
	protected final BlockPos offset;
	protected final int xSize;
	protected final int ySize;
	protected final int zSize;
	protected final WorldChunk[][] chunks;
	protected final BlockState[] blockStates;
	protected final FluidState[] fluidStates;
	protected final World world;

	@Nullable
	public static ChunkRendererRegion create(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
		int j = blockPos.getX() - i >> 4;
		int k = blockPos.getZ() - i >> 4;
		int l = blockPos2.getX() + i >> 4;
		int m = blockPos2.getZ() + i >> 4;
		WorldChunk[][] worldChunks = new WorldChunk[l - j + 1][m - k + 1];

		for (int n = j; n <= l; n++) {
			for (int o = k; o <= m; o++) {
				worldChunks[n - j][o - k] = world.method_8497(n, o);
			}
		}

		boolean bl = true;

		for (int o = blockPos.getX() >> 4; o <= blockPos2.getX() >> 4; o++) {
			for (int p = blockPos.getZ() >> 4; p <= blockPos2.getZ() >> 4; p++) {
				WorldChunk worldChunk = worldChunks[o - j][p - k];
				if (!worldChunk.method_12228(blockPos.getY(), blockPos2.getY())) {
					bl = false;
				}
			}
		}

		if (bl) {
			return null;
		} else {
			int o = 1;
			BlockPos blockPos3 = blockPos.add(-1, -1, -1);
			BlockPos blockPos4 = blockPos2.add(1, 1, 1);
			return new ChunkRendererRegion(world, j, k, worldChunks, blockPos3, blockPos4);
		}
	}

	public ChunkRendererRegion(World world, int i, int j, WorldChunk[][] worldChunks, BlockPos blockPos, BlockPos blockPos2) {
		this.world = world;
		this.chunkXOffset = i;
		this.chunkZOffset = j;
		this.chunks = worldChunks;
		this.offset = blockPos;
		this.xSize = blockPos2.getX() - blockPos.getX() + 1;
		this.ySize = blockPos2.getY() - blockPos.getY() + 1;
		this.zSize = blockPos2.getZ() - blockPos.getZ() + 1;
		this.blockStates = new BlockState[this.xSize * this.ySize * this.zSize];
		this.fluidStates = new FluidState[this.xSize * this.ySize * this.zSize];

		for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
			int k = (blockPos3.getX() >> 4) - i;
			int l = (blockPos3.getZ() >> 4) - j;
			WorldChunk worldChunk = worldChunks[k][l];
			int m = this.getIndex(blockPos3);
			this.blockStates[m] = worldChunk.getBlockState(blockPos3);
			this.fluidStates[m] = worldChunk.getFluidState(blockPos3);
		}
	}

	protected final int getIndex(BlockPos blockPos) {
		return this.getIndex(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	protected int getIndex(int i, int j, int k) {
		int l = i - this.offset.getX();
		int m = j - this.offset.getY();
		int n = k - this.offset.getZ();
		return n * this.xSize * this.ySize + m * this.xSize + l;
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		return this.blockStates[this.getIndex(blockPos)];
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		return this.fluidStates[this.getIndex(blockPos)];
	}

	@Override
	public int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.world.getLightLevel(lightType, blockPos);
	}

	@Override
	public Biome getBiome(BlockPos blockPos) {
		int i = (blockPos.getX() >> 4) - this.chunkXOffset;
		int j = (blockPos.getZ() >> 4) - this.chunkZOffset;
		return this.chunks[i][j].getBiome(blockPos);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		return this.getBlockEntity(blockPos, WorldChunk.CreationType.field_12860);
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos blockPos, WorldChunk.CreationType creationType) {
		int i = (blockPos.getX() >> 4) - this.chunkXOffset;
		int j = (blockPos.getZ() >> 4) - this.chunkZOffset;
		return this.chunks[i][j].getBlockEntity(blockPos, creationType);
	}
}
