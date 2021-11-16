package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.tick.OrderedTick;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

public class class_6748 {
	private static final class_6748 field_35501 = new class_6748(null, List.of(), List.of()) {
		@Override
		public TerrainNoisePoint method_39340(int i, int j, TerrainNoisePoint terrainNoisePoint) {
			return terrainNoisePoint;
		}

		@Override
		public double method_39338(int i, int j, int k, double d) {
			return d;
		}

		@Override
		public BiomeSupplier method_39563(BiomeSupplier biomeSupplier) {
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
	private static final double field_35506 = 10.0;
	private static final double field_35507 = 0.0;
	private final ChunkRegion field_35508;
	private final List<class_6748.class_6782> field_35509;
	private final List<class_6748.class_6782> field_35510;

	public static class_6748 method_39336() {
		return field_35501;
	}

	public static class_6748 method_39342(@Nullable ChunkRegion chunkRegion) {
		if (chunkRegion == null) {
			return field_35501;
		} else {
			List<class_6748.class_6782> list = Lists.<class_6748.class_6782>newArrayList();
			List<class_6748.class_6782> list2 = Lists.<class_6748.class_6782>newArrayList();
			ChunkPos chunkPos = chunkRegion.getCenterPos();

			for (int i = -field_35503; i <= field_35503; i++) {
				for (int j = -field_35503; j <= field_35503; j++) {
					int k = chunkPos.x + i;
					int l = chunkPos.z + j;
					Blender blender = Blender.method_39570(chunkRegion, k, l);
					if (blender != null) {
						class_6748.class_6782 lv = new class_6748.class_6782(k, l, blender);
						list.add(lv);
						if (i >= -field_35505 && i <= field_35505 && j >= -field_35505 && j <= field_35505) {
							list2.add(lv);
						}
					}
				}
			}

			return list.isEmpty() && list2.isEmpty() ? field_35501 : new class_6748(chunkRegion, list, list2);
		}
	}

	class_6748(ChunkRegion chunkRegion, List<class_6748.class_6782> list, List<class_6748.class_6782> list2) {
		this.field_35508 = chunkRegion;
		this.field_35509 = list;
		this.field_35510 = list2;
	}

	public TerrainNoisePoint method_39340(int i, int j, TerrainNoisePoint terrainNoisePoint) {
		int k = BiomeCoords.fromBlock(i);
		int l = BiomeCoords.fromBlock(j);
		double d = this.method_39562(k, 0, l, Blender::method_39344);
		if (d != Double.MAX_VALUE) {
			return new TerrainNoisePoint(method_39337(d), 10.0, 0.0);
		} else {
			MutableDouble mutableDouble = new MutableDouble(0.0);
			MutableDouble mutableDouble2 = new MutableDouble(0.0);
			MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);

			for (class_6748.class_6782 lv : this.field_35509) {
				lv.blendingData.method_39351(BiomeCoords.fromChunk(lv.chunkX), BiomeCoords.fromChunk(lv.chunkZ), (kx, lx, dx) -> {
					double e = MathHelper.hypot((double)(k - kx), (double)(l - lx));
					if (!(e > (double)field_35502)) {
						if (e < mutableDouble3.doubleValue()) {
							mutableDouble3.setValue(e);
						}

						double fx = 1.0 / (e * e * e * e);
						mutableDouble2.add(dx * fx);
						mutableDouble.add(fx);
					}
				});
			}

			if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
				return terrainNoisePoint;
			} else {
				double e = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
				double f = MathHelper.clamp(mutableDouble3.doubleValue() / (double)(field_35502 + 1), 0.0, 1.0);
				f = 3.0 * f * f - 2.0 * f * f * f;
				double g = MathHelper.lerp(f, method_39337(e), terrainNoisePoint.offset());
				double h = MathHelper.lerp(f, 10.0, terrainNoisePoint.factor());
				double m = MathHelper.lerp(f, 0.0, terrainNoisePoint.peaks());
				return new TerrainNoisePoint(g, h, m);
			}
		}
	}

	private static double method_39337(double d) {
		double e = 1.0;
		double f = d + 0.5;
		double g = MathHelper.floorMod(f, 8.0);
		return 1.0 * (32.0 * (f - 128.0) - 3.0 * (f - 120.0) * g + 3.0 * g * g) / (128.0 * (32.0 - 3.0 * g));
	}

	public double method_39338(int i, int j, int k, double d) {
		int l = BiomeCoords.fromBlock(i);
		int m = j / 8;
		int n = BiomeCoords.fromBlock(k);
		double e = this.method_39562(l, m, n, Blender::method_39345);
		if (e != Double.MAX_VALUE) {
			return e;
		} else {
			MutableDouble mutableDouble = new MutableDouble(0.0);
			MutableDouble mutableDouble2 = new MutableDouble(0.0);
			MutableDouble mutableDouble3 = new MutableDouble(Double.POSITIVE_INFINITY);

			for (class_6748.class_6782 lv : this.field_35510) {
				lv.blendingData.method_39346(BiomeCoords.fromChunk(lv.chunkX), BiomeCoords.fromChunk(lv.chunkZ), m - 2, m + 2, (lx, mx, nx, dx) -> {
					double ex = MathHelper.magnitude((double)(l - lx), (double)(m - mx), (double)(n - nx));
					if (!(ex > 2.0)) {
						if (ex < mutableDouble3.doubleValue()) {
							mutableDouble3.setValue(ex);
						}

						double f = 1.0 / (ex * ex * ex * ex);
						mutableDouble2.add(dx * f);
						mutableDouble.add(f);
					}
				});
			}

			if (mutableDouble3.doubleValue() == Double.POSITIVE_INFINITY) {
				return d;
			} else {
				double f = mutableDouble2.doubleValue() / mutableDouble.doubleValue();
				double g = MathHelper.clamp(mutableDouble3.doubleValue() / 3.0, 0.0, 1.0);
				return MathHelper.lerp(g, f, d);
			}
		}
	}

	private double method_39562(int i, int j, int k, class_6748.class_6781 arg) {
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

	private double method_39565(class_6748.class_6781 arg, int i, int j, int k, int l, int m) {
		Blender blender = Blender.method_39570(this.field_35508, i, j);
		return blender != null ? arg.get(blender, k - BiomeCoords.fromChunk(i), l, m - BiomeCoords.fromChunk(j)) : Double.MAX_VALUE;
	}

	public BiomeSupplier method_39563(BiomeSupplier biomeSupplier) {
		return (i, j, k, multiNoiseSampler) -> {
			Biome biome = this.method_39561(i, j, k);
			return biome == null ? biomeSupplier.getBiome(i, j, k, multiNoiseSampler) : biome;
		};
	}

	@Nullable
	private Biome method_39561(int i, int j, int k) {
		double d = (double)i + field_35681.sample((double)i, 0.0, (double)k) * 12.0;
		double e = (double)k + field_35681.sample((double)k, (double)i, 0.0) * 12.0;
		MutableDouble mutableDouble = new MutableDouble(Double.POSITIVE_INFINITY);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		MutableObject<ChunkPos> mutableObject = new MutableObject<>();

		for (class_6748.class_6782 lv : this.field_35509) {
			lv.blendingData.method_39351(BiomeCoords.fromChunk(lv.chunkX), BiomeCoords.fromChunk(lv.chunkZ), (ix, jx, f) -> {
				double g = MathHelper.hypot(d - (double)ix, e - (double)jx);
				if (!(g > (double)field_35502)) {
					if (g < mutableDouble.doubleValue()) {
						mutableObject.setValue(new ChunkPos(lv.chunkX, lv.chunkZ));
						mutable.set(ix, BiomeCoords.fromBlock(MathHelper.floor(f)), jx);
						mutableDouble.setValue(g);
					}
				}
			});
		}

		if (mutableDouble.doubleValue() == Double.POSITIVE_INFINITY) {
			return null;
		} else {
			double f = MathHelper.clamp(mutableDouble.doubleValue() / (double)(field_35502 + 1), 0.0, 1.0);
			if (f > 0.5) {
				return null;
			} else {
				Chunk chunk = this.field_35508.getChunk(mutableObject.getValue().x, mutableObject.getValue().z);
				return chunk.getBiomeForNoiseGen(Math.min(mutable.getX() & 3, 3), mutable.getY(), Math.min(mutable.getZ() & 3, 3));
			}
		}
	}

	public static void method_39772(ChunkRegion chunkRegion, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		boolean bl = chunk.usesOldNoise();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ());
		int i = Blender.OLD_HEIGHT_LIMIT.getBottomY();
		int j = Blender.OLD_HEIGHT_LIMIT.getTopY() - 1;
		if (bl) {
			for (int k = 0; k < 16; k++) {
				for (int l = 0; l < 16; l++) {
					method_39773(chunk, mutable.set(blockPos, k, i - 1, l));
					method_39773(chunk, mutable.set(blockPos, k, i, l));
					method_39773(chunk, mutable.set(blockPos, k, j, l));
					method_39773(chunk, mutable.set(blockPos, k, j + 1, l));
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
							method_39773(chunk, mutable.set(blockPos, q, t, r));
						}
					}
				}
			}
		}
	}

	private static void method_39773(Chunk chunk, BlockPos blockPos) {
		BlockState blockState = chunk.getBlockState(blockPos);
		if (blockState.isIn(BlockTags.LEAVES)) {
			chunk.getBlockTickScheduler().scheduleTick(OrderedTick.create(blockState.getBlock(), blockPos, 0L));
		}

		FluidState fluidState = chunk.getFluidState(blockPos);
		if (!fluidState.isEmpty()) {
			chunk.getFluidTickScheduler().scheduleTick(OrderedTick.create(fluidState.getFluid(), blockPos, 0L));
		}
	}

	interface class_6781 {
		double get(Blender blender, int i, int j, int k);
	}

	static record class_6782() {
		final int chunkX;
		final int chunkZ;
		final Blender blendingData;

		class_6782(int i, int j, Blender blender) {
			this.chunkX = i;
			this.chunkZ = j;
			this.blendingData = blender;
		}
	}
}
