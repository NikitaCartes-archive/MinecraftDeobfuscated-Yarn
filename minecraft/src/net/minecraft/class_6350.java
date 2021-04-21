package net.minecraft;

import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public interface class_6350 {
	int field_33571 = 9;
	int field_33572 = 30;

	static class_6350 method_36382(
		ChunkPos chunkPos,
		DoublePerlinNoiseSampler doublePerlinNoiseSampler,
		DoublePerlinNoiseSampler doublePerlinNoiseSampler2,
		DoublePerlinNoiseSampler doublePerlinNoiseSampler3,
		ChunkGeneratorSettings chunkGeneratorSettings,
		NoiseColumnSampler noiseColumnSampler,
		int i,
		int j
	) {
		return new class_6350.AquiferSampler(
			chunkPos, doublePerlinNoiseSampler, doublePerlinNoiseSampler2, doublePerlinNoiseSampler3, chunkGeneratorSettings, noiseColumnSampler, i, j
		);
	}

	static class_6350 method_36381(int i, BlockState blockState) {
		return new class_6350() {
			@Override
			public BlockState apply(BlockSource blockSource, int i, int j, int k, double d) {
				if (d > 0.0) {
					return blockSource.sample(i, j, k);
				} else {
					return j >= i ? Blocks.AIR.getDefaultState() : blockState;
				}
			}

			@Override
			public boolean needsFluidTick() {
				return false;
			}
		};
	}

	BlockState apply(BlockSource blockSource, int i, int j, int k, double d);

	boolean needsFluidTick();

	public static class AquiferSampler implements class_6350 {
		private static final int field_31451 = 10;
		private static final int field_31452 = 9;
		private static final int field_31453 = 10;
		private static final int field_31454 = 6;
		private static final int field_31455 = 3;
		private static final int field_31456 = 6;
		private static final int field_31457 = 16;
		private static final int field_31458 = 12;
		private static final int field_31459 = 16;
		private final DoublePerlinNoiseSampler edgeDensityNoise;
		private final DoublePerlinNoiseSampler waterLevelNoise;
		private final DoublePerlinNoiseSampler field_33575;
		private final ChunkGeneratorSettings settings;
		private final class_6350.AquiferSampler.class_6351[] waterLevels;
		private final long[] blockPositions;
		private boolean needsFluidTick;
		private final NoiseColumnSampler columnSampler;
		private final int startX;
		private final int startY;
		private final int startZ;
		private final int sizeX;
		private final int sizeZ;

		AquiferSampler(
			ChunkPos chunkPos,
			DoublePerlinNoiseSampler doublePerlinNoiseSampler,
			DoublePerlinNoiseSampler doublePerlinNoiseSampler2,
			DoublePerlinNoiseSampler doublePerlinNoiseSampler3,
			ChunkGeneratorSettings chunkGeneratorSettings,
			NoiseColumnSampler noiseColumnSampler,
			int i,
			int j
		) {
			this.edgeDensityNoise = doublePerlinNoiseSampler;
			this.waterLevelNoise = doublePerlinNoiseSampler2;
			this.field_33575 = doublePerlinNoiseSampler3;
			this.settings = chunkGeneratorSettings;
			this.columnSampler = noiseColumnSampler;
			this.startX = this.getLocalX(chunkPos.getStartX()) - 1;
			int k = this.getLocalX(chunkPos.getEndX()) + 1;
			this.sizeX = k - this.startX + 1;
			this.startY = this.getLocalY(i) - 1;
			int l = this.getLocalY(i + j) + 1;
			int m = l - this.startY + 1;
			this.startZ = this.getLocalZ(chunkPos.getStartZ()) - 1;
			int n = this.getLocalZ(chunkPos.getEndZ()) + 1;
			this.sizeZ = n - this.startZ + 1;
			int o = this.sizeX * m * this.sizeZ;
			this.waterLevels = new class_6350.AquiferSampler.class_6351[o];
			this.blockPositions = new long[o];
			Arrays.fill(this.blockPositions, Long.MAX_VALUE);
		}

		private int index(int x, int y, int z) {
			int i = x - this.startX;
			int j = y - this.startY;
			int k = z - this.startZ;
			return (j * this.sizeZ + k) * this.sizeX + i;
		}

		@Override
		public BlockState apply(BlockSource blockSource, int i, int j, int k, double d) {
			if (d <= 0.0) {
				double e;
				BlockState blockState;
				boolean bl;
				if (this.method_35324(j)) {
					blockState = Blocks.LAVA.getDefaultState();
					e = 0.0;
					bl = false;
				} else {
					int l = Math.floorDiv(i - 5, 16);
					int m = Math.floorDiv(j + 1, 12);
					int n = Math.floorDiv(k - 5, 16);
					int o = Integer.MAX_VALUE;
					int p = Integer.MAX_VALUE;
					int q = Integer.MAX_VALUE;
					long r = 0L;
					long s = 0L;
					long t = 0L;

					for (int u = 0; u <= 1; u++) {
						for (int v = -1; v <= 1; v++) {
							for (int w = 0; w <= 1; w++) {
								int x = l + u;
								int y = m + v;
								int z = n + w;
								int aa = this.index(x, y, z);
								long ab = this.blockPositions[aa];
								long ac;
								if (ab != Long.MAX_VALUE) {
									ac = ab;
								} else {
									ChunkRandom chunkRandom = new ChunkRandom(MathHelper.hashCode(x, y * 3, z) + 1L);
									ac = BlockPos.asLong(x * 16 + chunkRandom.nextInt(10), y * 12 + chunkRandom.nextInt(9), z * 16 + chunkRandom.nextInt(10));
									this.blockPositions[aa] = ac;
								}

								int ad = BlockPos.unpackLongX(ac) - i;
								int ae = BlockPos.unpackLongY(ac) - j;
								int af = BlockPos.unpackLongZ(ac) - k;
								int ag = ad * ad + ae * ae + af * af;
								if (o >= ag) {
									t = s;
									s = r;
									r = ac;
									q = p;
									p = o;
									o = ag;
								} else if (p >= ag) {
									t = s;
									s = ac;
									q = p;
									p = ag;
								} else if (q >= ag) {
									t = ac;
									q = ag;
								}
							}
						}
					}

					class_6350.AquiferSampler.class_6351 lv = this.getWaterLevel(r);
					class_6350.AquiferSampler.class_6351 lv2 = this.getWaterLevel(s);
					class_6350.AquiferSampler.class_6351 lv3 = this.getWaterLevel(t);
					double f = this.maxDistance(o, p);
					double g = this.maxDistance(o, q);
					double h = this.maxDistance(p, q);
					bl = f > 0.0;
					if (lv.field_33576 >= j && lv.field_33577.isOf(Blocks.WATER) && this.method_35324(j - 1)) {
						e = 1.0;
					} else if (f > -1.0) {
						double ah = 1.0 + (this.edgeDensityNoise.sample((double)i, (double)j, (double)k) + 0.05) / 4.0;
						double ai = this.calculateDensity(j, ah, lv, lv2);
						double aj = this.calculateDensity(j, ah, lv, lv3);
						double ak = this.calculateDensity(j, ah, lv2, lv3);
						double al = Math.max(0.0, f);
						double am = Math.max(0.0, g);
						double an = Math.max(0.0, h);
						double ao = 2.0 * al * Math.max(ai, Math.max(aj * am, ak * an));
						e = Math.max(0.0, ao);
					} else {
						e = 0.0;
					}

					blockState = j >= lv.field_33576 ? Blocks.AIR.getDefaultState() : lv.field_33577;
				}

				if (d + e <= 0.0) {
					this.needsFluidTick = bl;
					return blockState;
				}
			}

			this.needsFluidTick = false;
			return blockSource.sample(i, j, k);
		}

		@Override
		public boolean needsFluidTick() {
			return this.needsFluidTick;
		}

		private boolean method_35324(int i) {
			return i - this.settings.getGenerationShapeConfig().getMinimumY() <= 9;
		}

		private double maxDistance(int a, int b) {
			double d = 25.0;
			return 1.0 - (double)Math.abs(b - a) / 25.0;
		}

		private double calculateDensity(int y, double noise, class_6350.AquiferSampler.class_6351 arg, class_6350.AquiferSampler.class_6351 arg2) {
			if (y <= arg.field_33576 && y <= arg2.field_33576 && arg.field_33577 != arg2.field_33577) {
				return 1.0;
			} else {
				int i = Math.abs(arg.field_33576 - arg2.field_33576);
				double d = 0.5 * (double)(arg.field_33576 + arg2.field_33576);
				double e = Math.abs(d - (double)y - 0.5);
				return 0.5 * (double)i * noise - e;
			}
		}

		private int getLocalX(int x) {
			return Math.floorDiv(x, 16);
		}

		private int getLocalY(int y) {
			return Math.floorDiv(y, 12);
		}

		private int getLocalZ(int z) {
			return Math.floorDiv(z, 16);
		}

		private class_6350.AquiferSampler.class_6351 getWaterLevel(long pos) {
			int i = BlockPos.unpackLongX(pos);
			int j = BlockPos.unpackLongY(pos);
			int k = BlockPos.unpackLongZ(pos);
			int l = this.getLocalX(i);
			int m = this.getLocalY(j);
			int n = this.getLocalZ(k);
			int o = this.index(l, m, n);
			class_6350.AquiferSampler.class_6351 lv = this.waterLevels[o];
			if (lv != null) {
				return lv;
			} else {
				class_6350.AquiferSampler.class_6351 lv2 = this.getWaterLevel(i, j, k);
				this.waterLevels[o] = lv2;
				return lv2;
			}
		}

		private class_6350.AquiferSampler.class_6351 getWaterLevel(int x, int y, int z) {
			int i = this.settings.getSeaLevel();
			if (y > 30) {
				return new class_6350.AquiferSampler.class_6351(i, Blocks.WATER.getDefaultState());
			} else {
				int j = 64;
				int k = -10;
				int l = 40;
				double d = this.waterLevelNoise.sample((double)Math.floorDiv(x, 64), (double)Math.floorDiv(y, 40) / 1.4, (double)Math.floorDiv(z, 64)) * 30.0 + -10.0;
				boolean bl = false;
				if (Math.abs(d) > 8.0) {
					d *= 4.0;
				}

				int m = Math.floorDiv(y, 40) * 40 + 20;
				int n = m + MathHelper.floor(d);
				if (m == -20) {
					double e = this.field_33575.sample((double)Math.floorDiv(x, 64), (double)Math.floorDiv(y, 40) / 1.4, (double)Math.floorDiv(z, 64));
					bl = Math.abs(e) > 0.22F;
				}

				return new class_6350.AquiferSampler.class_6351(Math.min(56, n), bl ? Blocks.LAVA.getDefaultState() : Blocks.WATER.getDefaultState());
			}
		}

		static final class class_6351 {
			private final int field_33576;
			private final BlockState field_33577;

			public class_6351(int i, BlockState blockState) {
				this.field_33576 = i;
				this.field_33577 = blockState;
			}
		}
	}
}
