package net.minecraft.world.gen.chunk;

import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EightWayDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.chunk.Chunk;

public class BlendingData {
	private static final double field_35514 = 0.1;
	protected static final int field_36280 = 4;
	protected static final int field_35511 = 8;
	protected static final int field_36281 = 2;
	private static final double field_37704 = 1.0;
	private static final double field_37705 = -1.0;
	private static final int field_35516 = 2;
	private static final int BIOMES_PER_CHUNK = BiomeCoords.fromBlock(16);
	private static final int LAST_CHUNK_BIOME_INDEX = BIOMES_PER_CHUNK - 1;
	private static final int CHUNK_BIOME_END_INDEX = BIOMES_PER_CHUNK;
	private static final int NORTH_WEST_END_INDEX = 2 * LAST_CHUNK_BIOME_INDEX + 1;
	private static final int SOUTH_EAST_END_INDEX_PART = 2 * CHUNK_BIOME_END_INDEX + 1;
	private static final int HORIZONTAL_BIOME_COUNT = NORTH_WEST_END_INDEX + SOUTH_EAST_END_INDEX_PART;
	private final HeightLimitView oldHeightLimit;
	private static final List<Block> SURFACE_BLOCKS = List.of(
		Blocks.PODZOL,
		Blocks.GRAVEL,
		Blocks.GRASS_BLOCK,
		Blocks.STONE,
		Blocks.COARSE_DIRT,
		Blocks.SAND,
		Blocks.RED_SAND,
		Blocks.MYCELIUM,
		Blocks.SNOW_BLOCK,
		Blocks.TERRACOTTA,
		Blocks.DIRT
	);
	protected static final double field_35513 = Double.MAX_VALUE;
	private boolean initializedBlendingData;
	private final double[] surfaceHeights;
	private final List<List<RegistryEntry<Biome>>> biomes;
	private final transient double[][] collidableBlockDensities;
	private static final Codec<double[]> DOUBLE_ARRAY_CODEC = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
	public static final Codec<BlendingData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("min_section").forGetter(blendingData -> blendingData.oldHeightLimit.getBottomSectionCoord()),
						Codec.INT.fieldOf("max_section").forGetter(blendingData -> blendingData.oldHeightLimit.getTopSectionCoord()),
						DOUBLE_ARRAY_CODEC.optionalFieldOf("heights")
							.forGetter(
								blendingData -> DoubleStream.of(blendingData.surfaceHeights).anyMatch(height -> height != Double.MAX_VALUE)
										? Optional.of(blendingData.surfaceHeights)
										: Optional.empty()
							)
					)
					.apply(instance, BlendingData::new)
		)
		.comapFlatMap(BlendingData::validate, Function.identity());

	private static DataResult<BlendingData> validate(BlendingData data) {
		return data.surfaceHeights.length != HORIZONTAL_BIOME_COUNT
			? DataResult.error("heights has to be of length " + HORIZONTAL_BIOME_COUNT)
			: DataResult.success(data);
	}

	private BlendingData(int oldBottomSectionY, int oldTopSectionY, Optional<double[]> heights) {
		this.surfaceHeights = (double[])heights.orElse(Util.make(new double[HORIZONTAL_BIOME_COUNT], heights2 -> Arrays.fill(heights2, Double.MAX_VALUE)));
		this.collidableBlockDensities = new double[HORIZONTAL_BIOME_COUNT][];
		ObjectArrayList<List<RegistryEntry<Biome>>> objectArrayList = new ObjectArrayList<>(HORIZONTAL_BIOME_COUNT);
		objectArrayList.size(HORIZONTAL_BIOME_COUNT);
		this.biomes = objectArrayList;
		int i = ChunkSectionPos.getBlockCoord(oldBottomSectionY);
		int j = ChunkSectionPos.getBlockCoord(oldTopSectionY) - i;
		this.oldHeightLimit = HeightLimitView.create(i, j);
	}

	@Nullable
	public static BlendingData getBlendingData(ChunkRegion chunkRegion, int chunkX, int chunkZ) {
		Chunk chunk = chunkRegion.getChunk(chunkX, chunkZ);
		BlendingData blendingData = chunk.getBlendingData();
		if (blendingData == null) {
			return null;
		} else {
			blendingData.initChunkBlendingData(chunk, getAdjacentChunksWithNoise(chunkRegion, chunkX, chunkZ, false));
			return blendingData;
		}
	}

	public static Set<EightWayDirection> getAdjacentChunksWithNoise(StructureWorldAccess access, int chunkX, int chunkZ, boolean oldNoise) {
		Set<EightWayDirection> set = EnumSet.noneOf(EightWayDirection.class);

		for (EightWayDirection eightWayDirection : EightWayDirection.values()) {
			int i = chunkX + eightWayDirection.getOffsetX();
			int j = chunkZ + eightWayDirection.getOffsetZ();
			if (access.getChunk(i, j).usesOldNoise() == oldNoise) {
				set.add(eightWayDirection);
			}
		}

		return set;
	}

	private void initChunkBlendingData(Chunk chunk, Set<EightWayDirection> newNoiseChunkDirections) {
		if (!this.initializedBlendingData) {
			if (newNoiseChunkDirections.contains(EightWayDirection.NORTH)
				|| newNoiseChunkDirections.contains(EightWayDirection.WEST)
				|| newNoiseChunkDirections.contains(EightWayDirection.NORTH_WEST)) {
				this.initBlockColumn(getNorthWestIndex(0, 0), chunk, 0, 0);
			}

			if (newNoiseChunkDirections.contains(EightWayDirection.NORTH)) {
				for (int i = 1; i < BIOMES_PER_CHUNK; i++) {
					this.initBlockColumn(getNorthWestIndex(i, 0), chunk, 4 * i, 0);
				}
			}

			if (newNoiseChunkDirections.contains(EightWayDirection.WEST)) {
				for (int i = 1; i < BIOMES_PER_CHUNK; i++) {
					this.initBlockColumn(getNorthWestIndex(0, i), chunk, 0, 4 * i);
				}
			}

			if (newNoiseChunkDirections.contains(EightWayDirection.EAST)) {
				for (int i = 1; i < BIOMES_PER_CHUNK; i++) {
					this.initBlockColumn(getSouthEastIndex(CHUNK_BIOME_END_INDEX, i), chunk, 15, 4 * i);
				}
			}

			if (newNoiseChunkDirections.contains(EightWayDirection.SOUTH)) {
				for (int i = 0; i < BIOMES_PER_CHUNK; i++) {
					this.initBlockColumn(getSouthEastIndex(i, CHUNK_BIOME_END_INDEX), chunk, 4 * i, 15);
				}
			}

			if (newNoiseChunkDirections.contains(EightWayDirection.EAST) && newNoiseChunkDirections.contains(EightWayDirection.NORTH_EAST)) {
				this.initBlockColumn(getSouthEastIndex(CHUNK_BIOME_END_INDEX, 0), chunk, 15, 0);
			}

			if (newNoiseChunkDirections.contains(EightWayDirection.EAST)
				&& newNoiseChunkDirections.contains(EightWayDirection.SOUTH)
				&& newNoiseChunkDirections.contains(EightWayDirection.SOUTH_EAST)) {
				this.initBlockColumn(getSouthEastIndex(CHUNK_BIOME_END_INDEX, CHUNK_BIOME_END_INDEX), chunk, 15, 15);
			}

			this.initializedBlendingData = true;
		}
	}

	private void initBlockColumn(int index, Chunk chunk, int chunkBlockX, int chunkBlockZ) {
		if (this.surfaceHeights[index] == Double.MAX_VALUE) {
			this.surfaceHeights[index] = (double)this.getSurfaceBlockY(chunk, chunkBlockX, chunkBlockZ);
		}

		this.collidableBlockDensities[index] = this.calculateCollidableBlockDensityColumn(
			chunk, chunkBlockX, chunkBlockZ, MathHelper.floor(this.surfaceHeights[index])
		);
		this.biomes.set(index, this.getVerticalBiomeSections(chunk, chunkBlockX, chunkBlockZ));
	}

	private int getSurfaceBlockY(Chunk chunk, int blockX, int blockZ) {
		int i;
		if (chunk.hasHeightmap(Heightmap.Type.WORLD_SURFACE_WG)) {
			i = Math.min(chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, blockX, blockZ) + 1, this.oldHeightLimit.getTopY());
		} else {
			i = this.oldHeightLimit.getTopY();
		}

		int j = this.oldHeightLimit.getBottomY();
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockX, i, blockZ);

		while (mutable.getY() > j) {
			mutable.move(Direction.DOWN);
			if (SURFACE_BLOCKS.contains(chunk.getBlockState(mutable).getBlock())) {
				return mutable.getY();
			}
		}

		return j;
	}

	/**
	 * {@return {@code 1.0} if there is a collidable block below, otherwise {@code -1}}
	 * 
	 * @param mutablePos will be moved down by one block by this method
	 */
	private static double getAboveCollidableBlockValue(Chunk chunk, BlockPos.Mutable mutablePos) {
		return isCollidableAndNotTreeAt(chunk, mutablePos.move(Direction.DOWN)) ? 1.0 : -1.0;
	}

	/**
	 * @param mutablePos will be moved down by seven blocks by this method
	 */
	private static double getCollidableBlockDensityBelow(Chunk chunk, BlockPos.Mutable mutablePos) {
		double d = 0.0;

		for (int i = 0; i < 7; i++) {
			d += getAboveCollidableBlockValue(chunk, mutablePos);
		}

		return d;
	}

	private double[] calculateCollidableBlockDensityColumn(Chunk chunk, int chunkBlockX, int chunkBlockZ, int surfaceHeight) {
		double[] ds = new double[this.getVerticalHalfSectionCount()];
		Arrays.fill(ds, -1.0);
		BlockPos.Mutable mutable = new BlockPos.Mutable(chunkBlockX, this.oldHeightLimit.getTopY(), chunkBlockZ);
		double d = getCollidableBlockDensityBelow(chunk, mutable);

		for (int i = ds.length - 2; i >= 0; i--) {
			double e = getAboveCollidableBlockValue(chunk, mutable);
			double f = getCollidableBlockDensityBelow(chunk, mutable);
			ds[i] = (d + e + f) / 15.0;
			d = f;
		}

		int i = this.getHalfSectionHeight(MathHelper.floorDiv(surfaceHeight, 8));
		if (i >= 0 && i < ds.length - 1) {
			double e = ((double)surfaceHeight + 0.5) % 8.0 / 8.0;
			double f = (1.0 - e) / e;
			double g = Math.max(f, 1.0) * 0.25;
			ds[i + 1] = -f / g;
			ds[i] = 1.0 / g;
		}

		return ds;
	}

	private List<RegistryEntry<Biome>> getVerticalBiomeSections(Chunk chunk, int chunkBlockX, int chunkBlockZ) {
		ObjectArrayList<RegistryEntry<Biome>> objectArrayList = new ObjectArrayList<>(this.getVerticalBiomeCount());
		objectArrayList.size(this.getVerticalBiomeCount());

		for (int i = 0; i < objectArrayList.size(); i++) {
			int j = i + BiomeCoords.fromBlock(this.oldHeightLimit.getBottomY());
			objectArrayList.set(i, chunk.getBiomeForNoiseGen(BiomeCoords.fromBlock(chunkBlockX), j, BiomeCoords.fromBlock(chunkBlockZ)));
		}

		return objectArrayList;
	}

	private static boolean isCollidableAndNotTreeAt(Chunk chunk, BlockPos pos) {
		BlockState blockState = chunk.getBlockState(pos);
		if (blockState.isAir()) {
			return false;
		} else if (blockState.isIn(BlockTags.LEAVES)) {
			return false;
		} else if (blockState.isIn(BlockTags.LOGS)) {
			return false;
		} else {
			return blockState.isOf(Blocks.BROWN_MUSHROOM_BLOCK) || blockState.isOf(Blocks.RED_MUSHROOM_BLOCK)
				? false
				: !blockState.getCollisionShape(chunk, pos).isEmpty();
		}
	}

	protected double getHeight(int biomeX, int biomeY, int biomeZ) {
		if (biomeX == CHUNK_BIOME_END_INDEX || biomeZ == CHUNK_BIOME_END_INDEX) {
			return this.surfaceHeights[getSouthEastIndex(biomeX, biomeZ)];
		} else {
			return biomeX != 0 && biomeZ != 0 ? Double.MAX_VALUE : this.surfaceHeights[getNorthWestIndex(biomeX, biomeZ)];
		}
	}

	private double getCollidableBlockDensity(@Nullable double[] collidableBlockDensityColumn, int halfSectionY) {
		if (collidableBlockDensityColumn == null) {
			return Double.MAX_VALUE;
		} else {
			int i = this.getHalfSectionHeight(halfSectionY);
			return i >= 0 && i < collidableBlockDensityColumn.length ? collidableBlockDensityColumn[i] * 0.1 : Double.MAX_VALUE;
		}
	}

	protected double getCollidableBlockDensity(int chunkBiomeX, int halfSectionY, int chunkBiomeZ) {
		if (halfSectionY == this.getBottomHalfSectionY()) {
			return 0.1;
		} else if (chunkBiomeX == CHUNK_BIOME_END_INDEX || chunkBiomeZ == CHUNK_BIOME_END_INDEX) {
			return this.getCollidableBlockDensity(this.collidableBlockDensities[getSouthEastIndex(chunkBiomeX, chunkBiomeZ)], halfSectionY);
		} else {
			return chunkBiomeX != 0 && chunkBiomeZ != 0
				? Double.MAX_VALUE
				: this.getCollidableBlockDensity(this.collidableBlockDensities[getNorthWestIndex(chunkBiomeX, chunkBiomeZ)], halfSectionY);
		}
	}

	protected void acceptBiomes(int biomeX, int biomeY, int biomeZ, BlendingData.BiomeConsumer consumer) {
		if (biomeY >= BiomeCoords.fromBlock(this.oldHeightLimit.getBottomY()) && biomeY < BiomeCoords.fromBlock(this.oldHeightLimit.getTopY())) {
			int i = biomeY - BiomeCoords.fromBlock(this.oldHeightLimit.getBottomY());

			for (int j = 0; j < this.biomes.size(); j++) {
				if (this.biomes.get(j) != null) {
					RegistryEntry<Biome> registryEntry = (RegistryEntry<Biome>)((List)this.biomes.get(j)).get(i);
					if (registryEntry != null) {
						consumer.consume(biomeX + getX(j), biomeZ + getZ(j), registryEntry);
					}
				}
			}
		}
	}

	protected void acceptHeights(int biomeX, int biomeZ, BlendingData.HeightConsumer consumer) {
		for (int i = 0; i < this.surfaceHeights.length; i++) {
			double d = this.surfaceHeights[i];
			if (d != Double.MAX_VALUE) {
				consumer.consume(biomeX + getX(i), biomeZ + getZ(i), d);
			}
		}
	}

	protected void acceptCollidableBlockDensities(
		int biomeX, int biomeZ, int minHalfSectionY, int maxHalfSectionY, BlendingData.CollidableBlockDensityConsumer consumer
	) {
		int i = this.getOneAboveBottomHalfSectionY();
		int j = Math.max(0, minHalfSectionY - i);
		int k = Math.min(this.getVerticalHalfSectionCount(), maxHalfSectionY - i);

		for (int l = 0; l < this.collidableBlockDensities.length; l++) {
			double[] ds = this.collidableBlockDensities[l];
			if (ds != null) {
				int m = biomeX + getX(l);
				int n = biomeZ + getZ(l);

				for (int o = j; o < k; o++) {
					consumer.consume(m, o + i, n, ds[o] * 0.1);
				}
			}
		}
	}

	private int getVerticalHalfSectionCount() {
		return this.oldHeightLimit.countVerticalSections() * 2;
	}

	private int getVerticalBiomeCount() {
		return BiomeCoords.fromChunk(this.oldHeightLimit.countVerticalSections());
	}

	private int getOneAboveBottomHalfSectionY() {
		return this.getBottomHalfSectionY() + 1;
	}

	private int getBottomHalfSectionY() {
		return this.oldHeightLimit.getBottomSectionCoord() * 2;
	}

	private int getHalfSectionHeight(int halfSectionY) {
		return halfSectionY - this.getOneAboveBottomHalfSectionY();
	}

	/**
	 * Gets the north east index for the given chunk-local biome coordinates.
	 * At least one of these coordinates must be {@code 0} for this method to work properly.
	 * 
	 * @return the north west index
	 * 
	 * @param chunkBiomeX the chunk-local biome X coordinate
	 * @param chunkBiomeZ the chunk-local biome Z coordinate
	 */
	private static int getNorthWestIndex(int chunkBiomeX, int chunkBiomeZ) {
		return LAST_CHUNK_BIOME_INDEX - chunkBiomeX + chunkBiomeZ;
	}

	/**
	 * Gets the south east index for the given chunk-local biome coordinates.
	 * At least one of these coordinates must be {@code CHUNK_BIOME_END_INDEX} ({@code 4})
	 * for this method to work properly.
	 * 
	 * @return the south east index
	 * 
	 * @param chunkBiomeX the chunk-local biome X coordinate
	 * @param chunkBiomeZ the chunk-local biome Z coordinate
	 */
	private static int getSouthEastIndex(int chunkBiomeX, int chunkBiomeZ) {
		return NORTH_WEST_END_INDEX + chunkBiomeX + CHUNK_BIOME_END_INDEX - chunkBiomeZ;
	}

	private static int getX(int index) {
		if (index < NORTH_WEST_END_INDEX) {
			return method_39355(LAST_CHUNK_BIOME_INDEX - index);
		} else {
			int i = index - NORTH_WEST_END_INDEX;
			return CHUNK_BIOME_END_INDEX - method_39355(CHUNK_BIOME_END_INDEX - i);
		}
	}

	private static int getZ(int index) {
		if (index < NORTH_WEST_END_INDEX) {
			return method_39355(index - LAST_CHUNK_BIOME_INDEX);
		} else {
			int i = index - NORTH_WEST_END_INDEX;
			return CHUNK_BIOME_END_INDEX - method_39355(i - CHUNK_BIOME_END_INDEX);
		}
	}

	private static int method_39355(int i) {
		return i & ~(i >> 31);
	}

	public HeightLimitView getOldHeightLimit() {
		return this.oldHeightLimit;
	}

	protected interface BiomeConsumer {
		void consume(int biomeX, int biomeZ, RegistryEntry<Biome> biome);
	}

	protected interface CollidableBlockDensityConsumer {
		void consume(int biomeX, int halfSectionY, int biomeZ, double collidableBlockDensity);
	}

	protected interface HeightConsumer {
		void consume(int biomeX, int biomeZ, double height);
	}
}
