package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;

public interface class_4538 extends BlockRenderView, CollisionView, class_4543.class_4544 {
	@Nullable
	Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl);

	@Deprecated
	boolean isChunkLoaded(int i, int j);

	int getTopY(Heightmap.Type type, int i, int j);

	int getAmbientDarkness();

	@Override
	default Biome getBiome(int i, int j, int k) {
		Chunk chunk = this.getChunk(i >> 2, k >> 2, ChunkStatus.BIOMES, false);
		return chunk != null && chunk.getBiomeArray() != null ? chunk.getBiomeArray().getBiome(i, j, k) : this.method_22387(i, j, k);
	}

	Biome method_22387(int i, int j, int k);

	boolean isClient();

	int getSeaLevel();

	Dimension getDimension();

	default BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
		return new BlockPos(blockPos.getX(), this.getTopY(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
	}

	default boolean isAir(BlockPos blockPos) {
		return this.getBlockState(blockPos).isAir();
	}

	default boolean isSkyVisibleAllowingSea(BlockPos blockPos) {
		if (blockPos.getY() >= this.getSeaLevel()) {
			return this.isSkyVisible(blockPos);
		} else {
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());
			if (!this.isSkyVisible(blockPos2)) {
				return false;
			} else {
				for (BlockPos var4 = blockPos2.method_10074(); var4.getY() > blockPos.getY(); var4 = var4.method_10074()) {
					BlockState blockState = this.getBlockState(var4);
					if (blockState.getOpacity(this, var4) > 0 && !blockState.getMaterial().isLiquid()) {
						return false;
					}
				}

				return true;
			}
		}
	}

	default float getBrightness(BlockPos blockPos) {
		return this.getDimension().getLightLevelToBrightness()[this.getLightLevel(blockPos)];
	}

	default int getStrongRedstonePower(BlockPos blockPos, Direction direction) {
		return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
	}

	default Chunk getChunk(BlockPos blockPos) {
		return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	default Chunk getChunk(int i, int j) {
		return this.getChunk(i, j, ChunkStatus.FULL, true);
	}

	default Chunk getChunk(int i, int j, ChunkStatus chunkStatus) {
		return this.getChunk(i, j, chunkStatus, true);
	}

	@Nullable
	@Override
	default BlockView getExistingChunk(int i, int j) {
		return this.getChunk(i, j, ChunkStatus.EMPTY, false);
	}

	default boolean isWater(BlockPos blockPos) {
		return this.getFluidState(blockPos).matches(FluidTags.WATER);
	}

	default boolean containsFluid(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						BlockState blockState = this.getBlockState(pooledMutable.method_10113(o, p, q));
						if (!blockState.getFluidState().isEmpty()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	default int getLightLevel(BlockPos blockPos) {
		return this.getLightLevel(blockPos, this.getAmbientDarkness());
	}

	default int getLightLevel(BlockPos blockPos, int i) {
		return blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000
			? this.getBaseLightLevel(blockPos, i)
			: 15;
	}

	@Deprecated
	default boolean isChunkLoaded(BlockPos blockPos) {
		return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	@Deprecated
	default boolean isRegionLoaded(BlockPos blockPos, BlockPos blockPos2) {
		return this.isRegionLoaded(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
	}

	@Deprecated
	default boolean isRegionLoaded(int i, int j, int k, int l, int m, int n) {
		if (m >= 0 && j < 256) {
			i >>= 4;
			k >>= 4;
			l >>= 4;
			n >>= 4;

			for (int o = i; o <= l; o++) {
				for (int p = k; p <= n; p++) {
					if (!this.isChunkLoaded(o, p)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
