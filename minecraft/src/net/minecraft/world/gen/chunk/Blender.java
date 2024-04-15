package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EightWayDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.BuiltinNoiseParameters;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

public class Blender {
	private static final Blender NO_BLENDING = new Blender(new Long2ObjectOpenHashMap(), new Long2ObjectOpenHashMap()) {
		@Override
		public Blender.BlendResult calculate(int blockX, int blockZ) {
			return new Blender.BlendResult(1.0, 0.0);
		}

		@Override
		public double applyBlendDensity(DensityFunction.NoisePos pos, double density) {
			return density;
		}

		@Override
		public BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier) {
			return biomeSupplier;
		}
	};
	private static final DoublePerlinNoiseSampler OFFSET_NOISE = DoublePerlinNoiseSampler.create(
		new Xoroshiro128PlusPlusRandom(42L), BuiltinNoiseParameters.OFFSET
	);
	private static final int BLENDING_BIOME_DISTANCE_THRESHOLD = BiomeCoords.fromChunk(7) - 1;
	private static final int BLENDING_CHUNK_DISTANCE_THRESHOLD = BiomeCoords.toChunk(BLENDING_BIOME_DISTANCE_THRESHOLD + 3);
	private static final int field_35504 = 2;
	private static final int CLOSE_BLENDING_DISTANCE_THRESHOLD = BiomeCoords.toChunk(5);
	private static final double field_36224 = 8.0;
	private final Long2ObjectOpenHashMap<BlendingData> blendingData;
	private final Long2ObjectOpenHashMap<BlendingData> closeBlendingData;

	public static Blender getNoBlending() {
		return NO_BLENDING;
	}

	public static Blender getBlender(@Nullable ChunkRegion chunkRegion) {
		if (chunkRegion == null) {
			return NO_BLENDING;
		} else {
			ChunkPos chunkPos = chunkRegion.getCenterPos();
			if (!chunkRegion.needsBlending(chunkPos, BLENDING_CHUNK_DISTANCE_THRESHOLD)) {
				return NO_BLENDING;
			} else {
				Long2ObjectOpenHashMap<BlendingData> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap<>();
				Long2ObjectOpenHashMap<BlendingData> long2ObjectOpenHashMap2 = new Long2ObjectOpenHashMap<>();
				int i = MathHelper.square(BLENDING_CHUNK_DISTANCE_THRESHOLD + 1);

				for (int j = -BLENDING_CHUNK_DISTANCE_THRESHOLD; j <= BLENDING_CHUNK_DISTANCE_THRESHOLD; j++) {
					for (int k = -BLENDING_CHUNK_DISTANCE_THRESHOLD; k <= BLENDING_CHUNK_DISTANCE_THRESHOLD; k++) {
						if (j * j + k * k <= i) {
							int l = chunkPos.x + j;
							int m = chunkPos.z + k;
							BlendingData blendingData = BlendingData.getBlendingData(chunkRegion, l, m);
							if (blendingData != null) {
								long2ObjectOpenHashMap.put(ChunkPos.toLong(l, m), blendingData);
								if (j >= -CLOSE_BLENDING_DISTANCE_THRESHOLD
									&& j <= CLOSE_BLENDING_DISTANCE_THRESHOLD
									&& k >= -CLOSE_BLENDING_DISTANCE_THRESHOLD
									&& k <= CLOSE_BLENDING_DISTANCE_THRESHOLD) {
									long2ObjectOpenHashMap2.put(ChunkPos.toLong(l, m), blendingData);
								}
							}
						}
					}
				}

				return long2ObjectOpenHashMap.isEmpty() && long2ObjectOpenHashMap2.isEmpty() ? NO_BLENDING : new Blender(long2ObjectOpenHashMap, long2ObjectOpenHashMap2);
			}
		}
	}

	Blender(Long2ObjectOpenHashMap<BlendingData> blendingData, Long2ObjectOpenHashMap<BlendingData> closeBlendingData) {
		this.blendingData = blendingData;
		this.closeBlendingData = closeBlendingData;
	}

	public Blender.BlendResult calculate(int blockX, int blockZ) {
		int i = BiomeCoords.fromBlock(blockX);
		int j = BiomeCoords.fromBlock(blockZ);
		double d = this.sampleClosest(i, 0, j, BlendingData::getHeight);
		if (d != Double.MAX_VALUE) {
			return new Blender.BlendResult(0.0, getBlendOffset(d));
		} else {
			MutableDouble mutableDouble = new MutableDouble(0.0);
			MutableDouble mutableDouble2 = new MutableDouble(0.0);
			MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
			this.blendingData
				.forEach(
					(chunkPos, data) -> data.acceptHeights(
							BiomeCoords.fromChunk(ChunkPos.getPackedX(chunkPos)), BiomeCoords.fromChunk(ChunkPos.getPackedZ(chunkPos)), (biomeX, biomeZ, height) -> {
								double dx = MathHelper.hypot((double)(i - biomeX), (double)(j - biomeZ));
								if (!(dx > (double)BLENDING_BIOME_DISTANCE_THRESHOLD)) {
									if (dx < mutableDouble3.doubleValue()) {
										mutableDouble3.setValue(dx);
									}
			
									double ex = 1.0 / (dx * dx * dx * dx);
									mutableDouble2.add(height * ex);
									mutableDouble.add(ex);
								}
							}
						)
				);
			if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
				return new Blender.BlendResult(1.0, 0.0);
			} else {
				double e = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
				double f = MathHelper.clamp(mutableDouble3.doubleValue() / (double)(BLENDING_BIOME_DISTANCE_THRESHOLD + 1), 0.0, 1.0);
				f = 3.0 * f * f - 2.0 * f * f * f;
				return new Blender.BlendResult(f, getBlendOffset(e));
			}
		}
	}

	private static double getBlendOffset(double height) {
		double d = 1.0;
		double e = height + 0.5;
		double f = MathHelper.floorMod(e, 8.0);
		return 1.0 * (32.0 * (e - 128.0) - 3.0 * (e - 120.0) * f + 3.0 * f * f) / (128.0 * (32.0 - 3.0 * f));
	}

	public double applyBlendDensity(DensityFunction.NoisePos pos, double density) {
		int i = BiomeCoords.fromBlock(pos.blockX());
		int j = pos.blockY() / 8;
		int k = BiomeCoords.fromBlock(pos.blockZ());
		double d = this.sampleClosest(i, j, k, BlendingData::getCollidableBlockDensity);
		if (d != Double.MAX_VALUE) {
			return d;
		} else {
			MutableDouble mutableDouble = new MutableDouble(0.0);
			MutableDouble mutableDouble2 = new MutableDouble(0.0);
			MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
			this.closeBlendingData
				.forEach(
					(chunkPos, data) -> data.acceptCollidableBlockDensities(
							BiomeCoords.fromChunk(ChunkPos.getPackedX(chunkPos)),
							BiomeCoords.fromChunk(ChunkPos.getPackedZ(chunkPos)),
							j - 1,
							j + 1,
							(biomeX, halfSectionY, biomeZ, collidableBlockDensity) -> {
								double dx = MathHelper.magnitude((double)(i - biomeX), (double)((j - halfSectionY) * 2), (double)(k - biomeZ));
								if (!(dx > 2.0)) {
									if (dx < mutableDouble3.doubleValue()) {
										mutableDouble3.setValue(dx);
									}
			
									double ex = 1.0 / (dx * dx * dx * dx);
									mutableDouble2.add(collidableBlockDensity * ex);
									mutableDouble.add(ex);
								}
							}
						)
				);
			if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
				return density;
			} else {
				double e = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
				double f = MathHelper.clamp(mutableDouble3.doubleValue() / 3.0, 0.0, 1.0);
				return MathHelper.lerp(f, e, density);
			}
		}
	}

	private double sampleClosest(int biomeX, int biomeY, int biomeZ, Blender.BlendingSampler sampler) {
		int i = BiomeCoords.toChunk(biomeX);
		int j = BiomeCoords.toChunk(biomeZ);
		boolean bl = (biomeX & 3) == 0;
		boolean bl2 = (biomeZ & 3) == 0;
		double d = this.sample(sampler, i, j, biomeX, biomeY, biomeZ);
		if (d == Double.MAX_VALUE) {
			if (bl && bl2) {
				d = this.sample(sampler, i - 1, j - 1, biomeX, biomeY, biomeZ);
			}

			if (d == Double.MAX_VALUE) {
				if (bl) {
					d = this.sample(sampler, i - 1, j, biomeX, biomeY, biomeZ);
				}

				if (d == Double.MAX_VALUE && bl2) {
					d = this.sample(sampler, i, j - 1, biomeX, biomeY, biomeZ);
				}
			}
		}

		return d;
	}

	private double sample(Blender.BlendingSampler sampler, int chunkX, int chunkZ, int biomeX, int biomeY, int biomeZ) {
		BlendingData blendingData = this.blendingData.get(ChunkPos.toLong(chunkX, chunkZ));
		return blendingData != null
			? sampler.get(blendingData, biomeX - BiomeCoords.fromChunk(chunkX), biomeY, biomeZ - BiomeCoords.fromChunk(chunkZ))
			: Double.MAX_VALUE;
	}

	public BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier) {
		return (x, y, z, noise) -> {
			RegistryEntry<Biome> registryEntry = this.blendBiome(x, y, z);
			return registryEntry == null ? biomeSupplier.getBiome(x, y, z, noise) : registryEntry;
		};
	}

	@Nullable
	private RegistryEntry<Biome> blendBiome(int x, int y, int z) {
		MutableDouble mutableDouble = new MutableDouble(Double.POSITIVE_INFINITY);
		MutableObject<RegistryEntry<Biome>> mutableObject = new MutableObject<>();
		this.blendingData
			.forEach(
				(chunkPos, data) -> data.acceptBiomes(
						BiomeCoords.fromChunk(ChunkPos.getPackedX(chunkPos)), y, BiomeCoords.fromChunk(ChunkPos.getPackedZ(chunkPos)), (biomeX, biomeZ, biome) -> {
							double dx = MathHelper.hypot((double)(x - biomeX), (double)(z - biomeZ));
							if (!(dx > (double)BLENDING_BIOME_DISTANCE_THRESHOLD)) {
								if (dx < mutableDouble.doubleValue()) {
									mutableObject.setValue(biome);
									mutableDouble.setValue(dx);
								}
							}
						}
					)
			);
		if (mutableDouble.doubleValue() == Double.POSITIVE_INFINITY) {
			return null;
		} else {
			double d = OFFSET_NOISE.sample((double)x, 0.0, (double)z) * 12.0;
			double e = MathHelper.clamp((mutableDouble.doubleValue() + d) / (double)(BLENDING_BIOME_DISTANCE_THRESHOLD + 1), 0.0, 1.0);
			return e > 0.5 ? null : mutableObject.getValue();
		}
	}

	public static void tickLeavesAndFluids(ChunkRegion chunkRegion, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		boolean bl = chunk.usesOldNoise();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ());
		BlendingData blendingData = chunk.getBlendingData();
		if (blendingData != null) {
			int i = blendingData.getOldHeightLimit().getBottomY();
			int j = blendingData.getOldHeightLimit().getTopY() - 1;
			if (bl) {
				for (int k = 0; k < 16; k++) {
					for (int l = 0; l < 16; l++) {
						tickLeavesAndFluids(chunk, mutable.set(blockPos, k, i - 1, l));
						tickLeavesAndFluids(chunk, mutable.set(blockPos, k, i, l));
						tickLeavesAndFluids(chunk, mutable.set(blockPos, k, j, l));
						tickLeavesAndFluids(chunk, mutable.set(blockPos, k, j + 1, l));
					}
				}
			}

			for (Direction direction : Direction.Type.HORIZONTAL) {
				if (chunkRegion.getChunk(chunkPos.x + direction.getOffsetX(), chunkPos.z + direction.getOffsetZ()).usesOldNoise() != bl) {
					int m = direction == Direction.EAST ? 15 : 0;
					int n = direction == Direction.WEST ? 0 : 15;
					int o = direction == Direction.SOUTH ? 15 : 0;
					int p = direction == Direction.NORTH ? 0 : 15;

					for (int q = m; q <= n; q++) {
						for (int r = o; r <= p; r++) {
							int s = Math.min(j, chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, q, r)) + 1;

							for (int t = i; t < s; t++) {
								tickLeavesAndFluids(chunk, mutable.set(blockPos, q, t, r));
							}
						}
					}
				}
			}
		}
	}

	private static void tickLeavesAndFluids(Chunk chunk, BlockPos pos) {
		BlockState blockState = chunk.getBlockState(pos);
		if (blockState.isIn(BlockTags.LEAVES)) {
			chunk.markBlockForPostProcessing(pos);
		}

		FluidState fluidState = chunk.getFluidState(pos);
		if (!fluidState.isEmpty()) {
			chunk.markBlockForPostProcessing(pos);
		}
	}

	public static void createCarvingMasks(StructureWorldAccess world, ProtoChunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		Builder<EightWayDirection, BlendingData> builder = ImmutableMap.builder();

		for (EightWayDirection eightWayDirection : EightWayDirection.values()) {
			int i = chunkPos.x + eightWayDirection.getOffsetX();
			int j = chunkPos.z + eightWayDirection.getOffsetZ();
			BlendingData blendingData = world.getChunk(i, j).getBlendingData();
			if (blendingData != null) {
				builder.put(eightWayDirection, blendingData);
			}
		}

		ImmutableMap<EightWayDirection, BlendingData> immutableMap = builder.build();
		if (chunk.usesOldNoise() || !immutableMap.isEmpty()) {
			Blender.DistanceFunction distanceFunction = createClosestDistanceFunction(chunk.getBlendingData(), immutableMap);
			CarvingMask.MaskPredicate maskPredicate = (offsetX, y, offsetZ) -> {
				double d = (double)offsetX + 0.5 + OFFSET_NOISE.sample((double)offsetX, (double)y, (double)offsetZ) * 4.0;
				double e = (double)y + 0.5 + OFFSET_NOISE.sample((double)y, (double)offsetZ, (double)offsetX) * 4.0;
				double f = (double)offsetZ + 0.5 + OFFSET_NOISE.sample((double)offsetZ, (double)offsetX, (double)y) * 4.0;
				return distanceFunction.getDistance(d, e, f) < 4.0;
			};
			Stream.of(GenerationStep.Carver.values()).map(chunk::getOrCreateCarvingMask).forEach(mask -> mask.setMaskPredicate(maskPredicate));
		}
	}

	public static Blender.DistanceFunction createClosestDistanceFunction(@Nullable BlendingData data, Map<EightWayDirection, BlendingData> neighborData) {
		List<Blender.DistanceFunction> list = Lists.<Blender.DistanceFunction>newArrayList();
		if (data != null) {
			list.add(createDistanceFunction(null, data));
		}

		neighborData.forEach((direction, datax) -> list.add(createDistanceFunction(direction, datax)));
		return (offsetX, y, offsetZ) -> {
			double d = Double.POSITIVE_INFINITY;

			for (Blender.DistanceFunction distanceFunction : list) {
				double e = distanceFunction.getDistance(offsetX, y, offsetZ);
				if (e < d) {
					d = e;
				}
			}

			return d;
		};
	}

	private static Blender.DistanceFunction createDistanceFunction(@Nullable EightWayDirection direction, BlendingData data) {
		double d = 0.0;
		double e = 0.0;
		if (direction != null) {
			for (Direction direction2 : direction.getDirections()) {
				d += (double)(direction2.getOffsetX() * 16);
				e += (double)(direction2.getOffsetZ() * 16);
			}
		}

		double f = d;
		double g = e;
		double h = (double)data.getOldHeightLimit().getHeight() / 2.0;
		double i = (double)data.getOldHeightLimit().getBottomY() + h;
		return (offsetX, y, offsetZ) -> getDistance(offsetX - 8.0 - f, y - i, offsetZ - 8.0 - g, 8.0, h, 8.0);
	}

	private static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double d = Math.abs(x1) - x2;
		double e = Math.abs(y1) - y2;
		double f = Math.abs(z1) - z2;
		return MathHelper.magnitude(Math.max(0.0, d), Math.max(0.0, e), Math.max(0.0, f));
	}

	public static record BlendResult(double alpha, double blendingOffset) {
	}

	interface BlendingSampler {
		double get(BlendingData data, int biomeX, int biomeY, int biomeZ);
	}

	public interface DistanceFunction {
		double getDistance(double offsetX, double y, double offsetZ);
	}
}
