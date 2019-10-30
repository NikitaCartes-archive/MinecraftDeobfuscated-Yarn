package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;

public interface WorldView extends BlockRenderView, CollisionView, BiomeAccess.Storage {
	@Nullable
	Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);

	@Deprecated
	boolean isChunkLoaded(int chunkX, int chunkZ);

	int getTopY(Heightmap.Type heightmap, int x, int z);

	int getAmbientDarkness();

	@Override
	default Biome getStoredBiome(int biomeX, int biomeY, int biomeZ) {
		Chunk chunk = this.getChunk(biomeX >> 2, biomeZ >> 2, ChunkStatus.BIOMES, false);
		return chunk != null && chunk.getBiomeArray() != null
			? chunk.getBiomeArray().getStoredBiome(biomeX, biomeY, biomeZ)
			: this.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
	}

	Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ);

	boolean isClient();

	int getSeaLevel();

	Dimension getDimension();

	default BlockPos getTopPosition(Heightmap.Type heightmap, BlockPos pos) {
		return new BlockPos(pos.getX(), this.getTopY(heightmap, pos.getX(), pos.getZ()), pos.getZ());
	}

	default boolean isAir(BlockPos pos) {
		return this.getBlockState(pos).isAir();
	}

	default boolean isSkyVisibleAllowingSea(BlockPos pos) {
		if (pos.getY() >= this.getSeaLevel()) {
			return this.isSkyVisible(pos);
		} else {
			BlockPos blockPos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
			if (!this.isSkyVisible(blockPos)) {
				return false;
			} else {
				for (BlockPos var4 = blockPos.method_10074(); var4.getY() > pos.getY(); var4 = var4.method_10074()) {
					BlockState blockState = this.getBlockState(var4);
					if (blockState.getOpacity(this, var4) > 0 && !blockState.getMaterial().isLiquid()) {
						return false;
					}
				}

				return true;
			}
		}
	}

	default float getBrightness(BlockPos pos) {
		return this.getDimension().getLightLevelToBrightness()[this.getLightLevel(pos)];
	}

	default int getStrongRedstonePower(BlockPos pos, Direction direction) {
		return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
	}

	default Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	default Chunk getChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
	}

	default Chunk getChunk(int chunkX, int chunkZ, ChunkStatus status) {
		return this.getChunk(chunkX, chunkZ, status, true);
	}

	@Nullable
	@Override
	default BlockView getExistingChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
	}

	default boolean isWater(BlockPos pos) {
		return this.getFluidState(pos).matches(FluidTags.WATER);
	}

	default boolean containsFluid(Box box) {
		int i = MathHelper.floor(box.x1);
		int j = MathHelper.ceil(box.x2);
		int k = MathHelper.floor(box.y1);
		int l = MathHelper.ceil(box.y2);
		int m = MathHelper.floor(box.z1);
		int n = MathHelper.ceil(box.z2);

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

	default int getLightLevel(BlockPos pos) {
		return this.getLightLevel(pos, this.getAmbientDarkness());
	}

	default int getLightLevel(BlockPos pos, int ambientDarkness) {
		return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000
			? this.getBaseLightLevel(pos, ambientDarkness)
			: 15;
	}

	@Deprecated
	default boolean isChunkLoaded(BlockPos pos) {
		return this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
	}

	@Deprecated
	default boolean isRegionLoaded(BlockPos min, BlockPos max) {
		return this.isRegionLoaded(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}

	@Deprecated
	default boolean isRegionLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		if (maxY >= 0 && minY < 256) {
			minX >>= 4;
			minZ >>= 4;
			maxX >>= 4;
			maxZ >>= 4;

			for (int i = minX; i <= maxX; i++) {
				for (int j = minZ; j <= maxZ; j++) {
					if (!this.isChunkLoaded(i, j)) {
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
