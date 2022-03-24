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
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EightWayDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
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
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

public class Blender {
	private static final Blender NO_BLENDING = new Blender(new Long2ObjectOpenHashMap(), new Long2ObjectOpenHashMap()) {
		@Override
		public Blender.class_6956 method_39340(int i, int j) {
			return new Blender.class_6956(1.0, 0.0);
		}

		@Override
		public double method_39338(DensityFunction.NoisePos noisePos, double d) {
			return d;
		}

		@Override
		public BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier) {
			return biomeSupplier;
		}
	};
	private static final DoublePerlinNoiseSampler field_35681 = DoublePerlinNoiseSampler.create(
		new Xoroshiro128PlusPlusRandom(42L), BuiltinRegistries.NOISE_PARAMETERS.getOrThrow(NoiseParametersKeys.OFFSET)
	);
	private static final int field_35502 = BiomeCoords.fromChunk(7) - 1;
	private static final int field_35503 = BiomeCoords.toChunk(field_35502 + 3);
	private static final int field_35504 = 2;
	private static final int field_35505 = BiomeCoords.toChunk(5);
	private static final double field_36224 = 8.0;
	private final Long2ObjectOpenHashMap<BlendingData> field_36343;
	private final Long2ObjectOpenHashMap<BlendingData> field_36344;

	public static Blender getNoBlending() {
		return NO_BLENDING;
	}

	public static Blender getBlender(@Nullable ChunkRegion chunkRegion) {
		if (chunkRegion == null) {
			return NO_BLENDING;
		} else {
			ChunkPos chunkPos = chunkRegion.getCenterPos();
			if (!chunkRegion.needsBlending(chunkPos, field_35503)) {
				return NO_BLENDING;
			} else {
				Long2ObjectOpenHashMap<BlendingData> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap<>();
				Long2ObjectOpenHashMap<BlendingData> long2ObjectOpenHashMap2 = new Long2ObjectOpenHashMap<>();
				int i = MathHelper.square(field_35503 + 1);

				for (int j = -field_35503; j <= field_35503; j++) {
					for (int k = -field_35503; k <= field_35503; k++) {
						if (j * j + k * k <= i) {
							int l = chunkPos.x + j;
							int m = chunkPos.z + k;
							BlendingData blendingData = BlendingData.getBlendingData(chunkRegion, l, m);
							if (blendingData != null) {
								long2ObjectOpenHashMap.put(ChunkPos.toLong(l, m), blendingData);
								if (j >= -field_35505 && j <= field_35505 && k >= -field_35505 && k <= field_35505) {
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

	Blender(Long2ObjectOpenHashMap<BlendingData> long2ObjectOpenHashMap, Long2ObjectOpenHashMap<BlendingData> long2ObjectOpenHashMap2) {
		this.field_36343 = long2ObjectOpenHashMap;
		this.field_36344 = long2ObjectOpenHashMap2;
	}

	public Blender.class_6956 method_39340(int i, int j) {
		int k = BiomeCoords.fromBlock(i);
		int l = BiomeCoords.fromBlock(j);
		double d = this.method_39562(k, 0, l, BlendingData::method_39344);
		if (d != Double.MAX_VALUE) {
			return new Blender.class_6956(0.0, method_39337(d));
		} else {
			MutableDouble mutableDouble = new MutableDouble(0.0);
			MutableDouble mutableDouble2 = new MutableDouble(0.0);
			MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
			this.field_36343
				.forEach(
					(long_, blendingData) -> blendingData.method_39351(
							BiomeCoords.fromChunk(ChunkPos.getPackedX(long_)), BiomeCoords.fromChunk(ChunkPos.getPackedZ(long_)), (kx, lx, dx) -> {
								double ex = MathHelper.hypot((double)(k - kx), (double)(l - lx));
								if (!(ex > (double)field_35502)) {
									if (ex < mutableDouble3.doubleValue()) {
										mutableDouble3.setValue(ex);
									}

									double fx = 1.0 / (ex * ex * ex * ex);
									mutableDouble2.add(dx * fx);
									mutableDouble.add(fx);
								}
							}
						)
				);
			if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
				return new Blender.class_6956(1.0, 0.0);
			} else {
				double e = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
				double f = MathHelper.clamp(mutableDouble3.doubleValue() / (double)(field_35502 + 1), 0.0, 1.0);
				f = 3.0 * f * f - 2.0 * f * f * f;
				return new Blender.class_6956(f, method_39337(e));
			}
		}
	}

	private static double method_39337(double d) {
		double e = 1.0;
		double f = d + 0.5;
		double g = MathHelper.floorMod(f, 8.0);
		return 1.0 * (32.0 * (f - 128.0) - 3.0 * (f - 120.0) * g + 3.0 * g * g) / (128.0 * (32.0 - 3.0 * g));
	}

	public double method_39338(DensityFunction.NoisePos noisePos, double d) {
		int i = BiomeCoords.fromBlock(noisePos.blockX());
		int j = noisePos.blockY() / 8;
		int k = BiomeCoords.fromBlock(noisePos.blockZ());
		double e = this.method_39562(i, j, k, BlendingData::method_39345);
		if (e != Double.MAX_VALUE) {
			return e;
		} else {
			MutableDouble mutableDouble = new MutableDouble(0.0);
			MutableDouble mutableDouble2 = new MutableDouble(0.0);
			MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);
			this.field_36344
				.forEach(
					(long_, blendingData) -> blendingData.method_39346(
							BiomeCoords.fromChunk(ChunkPos.getPackedX(long_)), BiomeCoords.fromChunk(ChunkPos.getPackedZ(long_)), j - 1, j + 1, (l, m, n, dx) -> {
								double ex = MathHelper.magnitude((double)(i - l), (double)((j - m) * 2), (double)(k - n));
								if (!(ex > 2.0)) {
									if (ex < mutableDouble3.doubleValue()) {
										mutableDouble3.setValue(ex);
									}

									double fx = 1.0 / (ex * ex * ex * ex);
									mutableDouble2.add(dx * fx);
									mutableDouble.add(fx);
								}
							}
						)
				);
			if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
				return d;
			} else {
				double f = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
				double g = MathHelper.clamp(mutableDouble3.doubleValue() / 3.0, 0.0, 1.0);
				return MathHelper.lerp(g, f, d);
			}
		}
	}

	private double method_39562(int i, int j, int k, Blender.class_6781 arg) {
		int l = BiomeCoords.toChunk(i);
		int m = BiomeCoords.toChunk(k);
		boolean bl = (i & 3) == 0;
		boolean bl2 = (k & 3) == 0;
		double d = this.method_39565(arg, l, m, i, j, k);
		if (d == Double.MAX_VALUE) {
			if (bl && bl2) {
				d = this.method_39565(arg, l - 1, m - 1, i, j, k);
			}

			if (d == Double.MAX_VALUE) {
				if (bl) {
					d = this.method_39565(arg, l - 1, m, i, j, k);
				}

				if (d == Double.MAX_VALUE && bl2) {
					d = this.method_39565(arg, l, m - 1, i, j, k);
				}
			}
		}

		return d;
	}

	private double method_39565(Blender.class_6781 arg, int i, int j, int k, int l, int m) {
		BlendingData blendingData = this.field_36343.get(ChunkPos.toLong(i, j));
		return blendingData != null ? arg.get(blendingData, k - BiomeCoords.fromChunk(i), l, m - BiomeCoords.fromChunk(j)) : Double.MAX_VALUE;
	}

	public BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier) {
		return (x, y, z, noise) -> {
			RegistryEntry<Biome> registryEntry = this.blendBiome(x, y, z);
			return registryEntry == null ? biomeSupplier.getBiome(x, y, z, noise) : registryEntry;
		};
	}

	@Nullable
	private RegistryEntry<Biome> blendBiome(int x, int y, int i) {
		MutableDouble mutableDouble = new MutableDouble(Double.POSITIVE_INFINITY);
		MutableObject<RegistryEntry<Biome>> mutableObject = new MutableObject<>();
		this.field_36343
			.forEach(
				(long_, blendingData) -> blendingData.method_40028(
						BiomeCoords.fromChunk(ChunkPos.getPackedX(long_)), y, BiomeCoords.fromChunk(ChunkPos.getPackedZ(long_)), (kx, l, registryEntry) -> {
							double dx = MathHelper.hypot((double)(x - kx), (double)(i - l));
							if (!(dx > (double)field_35502)) {
								if (dx < mutableDouble.doubleValue()) {
									mutableObject.setValue(registryEntry);
									mutableDouble.setValue(dx);
								}
							}
						}
					)
			);
		if (mutableDouble.doubleValue() == Double.POSITIVE_INFINITY) {
			return null;
		} else {
			double d = field_35681.sample((double)x, 0.0, (double)i) * 12.0;
			double e = MathHelper.clamp((mutableDouble.doubleValue() + d) / (double)(field_35502 + 1), 0.0, 1.0);
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
			int i = blendingData.method_41564().getBottomY();
			int j = blendingData.method_41564().getTopY() - 1;
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

	public static void method_39809(StructureWorldAccess structureWorldAccess, ProtoChunk protoChunk) {
		ChunkPos chunkPos = protoChunk.getPos();
		Builder<EightWayDirection, BlendingData> builder = ImmutableMap.builder();

		for (EightWayDirection eightWayDirection : EightWayDirection.values()) {
			int i = chunkPos.x + eightWayDirection.method_42015();
			int j = chunkPos.z + eightWayDirection.method_42016();
			BlendingData blendingData = structureWorldAccess.getChunk(i, j).getBlendingData();
			if (blendingData != null) {
				builder.put(eightWayDirection, blendingData);
			}
		}

		ImmutableMap<EightWayDirection, BlendingData> immutableMap = builder.build();
		if (protoChunk.usesOldNoise() || !immutableMap.isEmpty()) {
			Blender.class_6831 lv = method_39815(protoChunk.getBlendingData(), immutableMap);
			CarvingMask.MaskPredicate maskPredicate = (ix, jx, k) -> {
				double d = (double)ix + 0.5 + field_35681.sample((double)ix, (double)jx, (double)k) * 4.0;
				double e = (double)jx + 0.5 + field_35681.sample((double)jx, (double)k, (double)ix) * 4.0;
				double f = (double)k + 0.5 + field_35681.sample((double)k, (double)ix, (double)jx) * 4.0;
				return lv.getDistance(d, e, f) < 4.0;
			};
			Stream.of(GenerationStep.Carver.values()).map(protoChunk::getOrCreateCarvingMask).forEach(carvingMask -> carvingMask.setMaskPredicate(maskPredicate));
		}
	}

	public static Blender.class_6831 method_39815(@Nullable BlendingData blendingData, Map<EightWayDirection, BlendingData> map) {
		List<Blender.class_6831> list = Lists.<Blender.class_6831>newArrayList();
		if (blendingData != null) {
			list.add(method_39812(null, blendingData));
		}

		map.forEach((eightWayDirection, blendingDatax) -> list.add(method_39812(eightWayDirection, blendingDatax)));
		return (d, e, f) -> {
			double g = Double.POSITIVE_INFINITY;

			for (Blender.class_6831 lv : list) {
				double h = lv.getDistance(d, e, f);
				if (h < g) {
					g = h;
				}
			}

			return g;
		};
	}

	private static Blender.class_6831 method_39812(@Nullable EightWayDirection eightWayDirection, BlendingData blendingData) {
		double d = 0.0;
		double e = 0.0;
		if (eightWayDirection != null) {
			for (Direction direction : eightWayDirection.getDirections()) {
				d += (double)(direction.getOffsetX() * 16);
				e += (double)(direction.getOffsetZ() * 16);
			}
		}

		double f = d;
		double g = e;
		double h = (double)blendingData.method_41564().getHeight() / 2.0;
		double i = (double)blendingData.method_41564().getBottomY() + h;
		return (hx, ix, j) -> method_39808(hx - 8.0 - f, ix - i, j - 8.0 - g, 8.0, h, 8.0);
	}

	private static double method_39808(double d, double e, double f, double g, double h, double i) {
		double j = Math.abs(d) - g;
		double k = Math.abs(e) - h;
		double l = Math.abs(f) - i;
		return MathHelper.magnitude(Math.max(0.0, j), Math.max(0.0, k), Math.max(0.0, l));
	}

	interface class_6781 {
		double get(BlendingData blendingData, int i, int j, int k);
	}

	public interface class_6831 {
		double getDistance(double d, double e, double f);
	}

	public static record class_6956(double alpha, double blendingOffset) {
	}
}
