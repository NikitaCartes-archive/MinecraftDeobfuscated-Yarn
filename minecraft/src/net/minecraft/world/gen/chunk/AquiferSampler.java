package net.minecraft.world.gen.chunk;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseRouter;
import org.apache.commons.lang3.mutable.MutableDouble;

public interface AquiferSampler {
	static AquiferSampler aquifer(
		ChunkNoiseSampler chunkNoiseSampler,
		ChunkPos chunkPos,
		NoiseRouter noiseRouter,
		RandomSplitter randomSplitter,
		int i,
		int j,
		AquiferSampler.FluidLevelSampler fluidLevelSampler
	) {
		return new AquiferSampler.Impl(chunkNoiseSampler, chunkPos, noiseRouter, randomSplitter, i, j, fluidLevelSampler);
	}

	static AquiferSampler seaLevel(AquiferSampler.FluidLevelSampler fluidLevelSampler) {
		return new AquiferSampler() {
			@Nullable
			@Override
			public BlockState apply(DensityFunction.NoisePos noisePos, double d) {
				return d > 0.0 ? null : fluidLevelSampler.getFluidLevel(noisePos.blockX(), noisePos.blockY(), noisePos.blockZ()).getBlockState(noisePos.blockY());
			}

			@Override
			public boolean needsFluidTick() {
				return false;
			}
		};
	}

	@Nullable
	BlockState apply(DensityFunction.NoisePos noisePos, double d);

	boolean needsFluidTick();

	public static final class FluidLevel {
		final int y;
		final BlockState state;

		public FluidLevel(int y, BlockState state) {
			this.y = y;
			this.state = state;
		}

		public BlockState getBlockState(int y) {
			return y < this.y ? this.state : Blocks.AIR.getDefaultState();
		}
	}

	public interface FluidLevelSampler {
		AquiferSampler.FluidLevel getFluidLevel(int x, int y, int z);
	}

	public static class Impl implements AquiferSampler {
		private static final int field_31451 = 10;
		private static final int field_31452 = 9;
		private static final int field_31453 = 10;
		private static final int field_31454 = 6;
		private static final int field_31455 = 3;
		private static final int field_31456 = 6;
		private static final int field_31457 = 16;
		private static final int field_31458 = 12;
		private static final int field_31459 = 16;
		private static final int field_36220 = 11;
		private static final double field_36221 = maxDistance(MathHelper.square(10), MathHelper.square(12));
		private final ChunkNoiseSampler chunkNoiseSampler;
		private final DensityFunction barrierNoise;
		private final DensityFunction fluidLevelFloodednessNoise;
		private final DensityFunction fluidLevelSpreadNoise;
		private final DensityFunction fluidTypeNoise;
		private final RandomSplitter randomDeriver;
		private final AquiferSampler.FluidLevel[] waterLevels;
		private final long[] blockPositions;
		private final AquiferSampler.FluidLevelSampler fluidLevelSampler;
		private final DensityFunction field_38246;
		private final DensityFunction field_38247;
		private boolean needsFluidTick;
		private final int startX;
		private final int startY;
		private final int startZ;
		private final int sizeX;
		private final int sizeZ;
		private static final int[][] field_34581 = new int[][]{
			{-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}
		};

		Impl(
			ChunkNoiseSampler chunkNoiseSampler,
			ChunkPos chunkPos,
			NoiseRouter noiseRouter,
			RandomSplitter randomSplitter,
			int i,
			int j,
			AquiferSampler.FluidLevelSampler fluidLevelSampler
		) {
			this.chunkNoiseSampler = chunkNoiseSampler;
			this.barrierNoise = noiseRouter.barrierNoise();
			this.fluidLevelFloodednessNoise = noiseRouter.fluidLevelFloodednessNoise();
			this.fluidLevelSpreadNoise = noiseRouter.fluidLevelSpreadNoise();
			this.fluidTypeNoise = noiseRouter.lavaNoise();
			this.field_38246 = noiseRouter.erosion();
			this.field_38247 = noiseRouter.depth();
			this.randomDeriver = randomSplitter;
			this.startX = this.getLocalX(chunkPos.getStartX()) - 1;
			this.fluidLevelSampler = fluidLevelSampler;
			int k = this.getLocalX(chunkPos.getEndX()) + 1;
			this.sizeX = k - this.startX + 1;
			this.startY = this.getLocalY(i) - 1;
			int l = this.getLocalY(i + j) + 1;
			int m = l - this.startY + 1;
			this.startZ = this.getLocalZ(chunkPos.getStartZ()) - 1;
			int n = this.getLocalZ(chunkPos.getEndZ()) + 1;
			this.sizeZ = n - this.startZ + 1;
			int o = this.sizeX * m * this.sizeZ;
			this.waterLevels = new AquiferSampler.FluidLevel[o];
			this.blockPositions = new long[o];
			Arrays.fill(this.blockPositions, Long.MAX_VALUE);
		}

		private int index(int x, int y, int z) {
			int i = x - this.startX;
			int j = y - this.startY;
			int k = z - this.startZ;
			return (j * this.sizeZ + k) * this.sizeX + i;
		}

		@Nullable
		@Override
		public BlockState apply(DensityFunction.NoisePos noisePos, double d) {
			int i = noisePos.blockX();
			int j = noisePos.blockY();
			int k = noisePos.blockZ();
			if (d > 0.0) {
				this.needsFluidTick = false;
				return null;
			} else {
				AquiferSampler.FluidLevel fluidLevel = this.fluidLevelSampler.getFluidLevel(i, j, k);
				if (fluidLevel.getBlockState(j).isOf(Blocks.LAVA)) {
					this.needsFluidTick = false;
					return Blocks.LAVA.getDefaultState();
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
									Random random = this.randomDeriver.split(x, y, z);
									ac = BlockPos.asLong(x * 16 + random.nextInt(10), y * 12 + random.nextInt(9), z * 16 + random.nextInt(10));
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

					AquiferSampler.FluidLevel fluidLevel2 = this.getWaterLevel(r);
					double e = maxDistance(o, p);
					BlockState blockState = fluidLevel2.getBlockState(j);
					if (e <= 0.0) {
						this.needsFluidTick = e >= field_36221;
						return blockState;
					} else if (blockState.isOf(Blocks.WATER) && this.fluidLevelSampler.getFluidLevel(i, j - 1, k).getBlockState(j - 1).isOf(Blocks.LAVA)) {
						this.needsFluidTick = true;
						return blockState;
					} else {
						MutableDouble mutableDouble = new MutableDouble(Double.NaN);
						AquiferSampler.FluidLevel fluidLevel3 = this.getWaterLevel(s);
						double f = e * this.calculateDensity(noisePos, mutableDouble, fluidLevel2, fluidLevel3);
						if (d + f > 0.0) {
							this.needsFluidTick = false;
							return null;
						} else {
							AquiferSampler.FluidLevel fluidLevel4 = this.getWaterLevel(t);
							double g = maxDistance(o, q);
							if (g > 0.0) {
								double h = e * g * this.calculateDensity(noisePos, mutableDouble, fluidLevel2, fluidLevel4);
								if (d + h > 0.0) {
									this.needsFluidTick = false;
									return null;
								}
							}

							double h = maxDistance(p, q);
							if (h > 0.0) {
								double ah = e * h * this.calculateDensity(noisePos, mutableDouble, fluidLevel3, fluidLevel4);
								if (d + ah > 0.0) {
									this.needsFluidTick = false;
									return null;
								}
							}

							this.needsFluidTick = true;
							return blockState;
						}
					}
				}
			}
		}

		@Override
		public boolean needsFluidTick() {
			return this.needsFluidTick;
		}

		private static double maxDistance(int i, int a) {
			double d = 25.0;
			return 1.0 - (double)Math.abs(a - i) / 25.0;
		}

		private double calculateDensity(
			DensityFunction.NoisePos noisePos, MutableDouble mutableDouble, AquiferSampler.FluidLevel fluidLevel, AquiferSampler.FluidLevel fluidLevel2
		) {
			int i = noisePos.blockY();
			BlockState blockState = fluidLevel.getBlockState(i);
			BlockState blockState2 = fluidLevel2.getBlockState(i);
			if ((!blockState.isOf(Blocks.LAVA) || !blockState2.isOf(Blocks.WATER)) && (!blockState.isOf(Blocks.WATER) || !blockState2.isOf(Blocks.LAVA))) {
				int j = Math.abs(fluidLevel.y - fluidLevel2.y);
				if (j == 0) {
					return 0.0;
				} else {
					double d = 0.5 * (double)(fluidLevel.y + fluidLevel2.y);
					double e = (double)i + 0.5 - d;
					double f = (double)j / 2.0;
					double g = 0.0;
					double h = 2.5;
					double k = 1.5;
					double l = 3.0;
					double m = 10.0;
					double n = 3.0;
					double o = f - Math.abs(e);
					double q;
					if (e > 0.0) {
						double p = 0.0 + o;
						if (p > 0.0) {
							q = p / 1.5;
						} else {
							q = p / 2.5;
						}
					} else {
						double p = 3.0 + o;
						if (p > 0.0) {
							q = p / 3.0;
						} else {
							q = p / 10.0;
						}
					}

					double px = 2.0;
					double r;
					if (!(q < -2.0) && !(q > 2.0)) {
						double s = mutableDouble.getValue();
						if (Double.isNaN(s)) {
							double t = this.barrierNoise.sample(noisePos);
							mutableDouble.setValue(t);
							r = t;
						} else {
							r = s;
						}
					} else {
						r = 0.0;
					}

					return 2.0 * (r + q);
				}
			} else {
				return 2.0;
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

		private AquiferSampler.FluidLevel getWaterLevel(long pos) {
			int i = BlockPos.unpackLongX(pos);
			int j = BlockPos.unpackLongY(pos);
			int k = BlockPos.unpackLongZ(pos);
			int l = this.getLocalX(i);
			int m = this.getLocalY(j);
			int n = this.getLocalZ(k);
			int o = this.index(l, m, n);
			AquiferSampler.FluidLevel fluidLevel = this.waterLevels[o];
			if (fluidLevel != null) {
				return fluidLevel;
			} else {
				AquiferSampler.FluidLevel fluidLevel2 = this.method_40463(i, j, k);
				this.waterLevels[o] = fluidLevel2;
				return fluidLevel2;
			}
		}

		private AquiferSampler.FluidLevel method_40463(int i, int j, int k) {
			AquiferSampler.FluidLevel fluidLevel = this.fluidLevelSampler.getFluidLevel(i, j, k);
			int l = Integer.MAX_VALUE;
			int m = j + 12;
			int n = j - 12;
			boolean bl = false;

			for (int[] is : field_34581) {
				int o = i + ChunkSectionPos.getBlockCoord(is[0]);
				int p = k + ChunkSectionPos.getBlockCoord(is[1]);
				int q = this.chunkNoiseSampler.method_39900(o, p);
				int r = q + 8;
				boolean bl2 = is[0] == 0 && is[1] == 0;
				if (bl2 && n > r) {
					return fluidLevel;
				}

				boolean bl3 = m > r;
				if (bl3 || bl2) {
					AquiferSampler.FluidLevel fluidLevel2 = this.fluidLevelSampler.getFluidLevel(o, r, p);
					if (!fluidLevel2.getBlockState(r).isAir()) {
						if (bl2) {
							bl = true;
						}

						if (bl3) {
							return fluidLevel2;
						}
					}
				}

				l = Math.min(l, q);
			}

			int s = this.method_42354(i, j, k, fluidLevel, l, bl);
			return new AquiferSampler.FluidLevel(s, this.method_42353(i, j, k, fluidLevel, s));
		}

		private int method_42354(int i, int j, int k, AquiferSampler.FluidLevel fluidLevel, int l, boolean bl) {
			DensityFunction.UnblendedNoisePos unblendedNoisePos = new DensityFunction.UnblendedNoisePos(i, j, k);
			double d;
			double e;
			if (VanillaBiomeParameters.method_43718(this.field_38246.sample(unblendedNoisePos), this.field_38247.sample(unblendedNoisePos))) {
				d = -1.0;
				e = -1.0;
			} else {
				int m = l + 8 - j;
				int n = 64;
				double f = bl ? MathHelper.clampedLerpFromProgress((double)m, 0.0, 64.0, 1.0, 0.0) : 0.0;
				double g = MathHelper.clamp(this.fluidLevelFloodednessNoise.sample(unblendedNoisePos), -1.0, 1.0);
				double h = MathHelper.lerpFromProgress(f, 1.0, 0.0, -0.3, 0.8);
				double o = MathHelper.lerpFromProgress(f, 1.0, 0.0, -0.8, 0.4);
				d = g - o;
				e = g - h;
			}

			int m;
			if (e > 0.0) {
				m = fluidLevel.y;
			} else if (d > 0.0) {
				m = this.method_42352(i, j, k, l);
			} else {
				m = DimensionType.field_35479;
			}

			return m;
		}

		private int method_42352(int i, int j, int k, int l) {
			int m = 16;
			int n = 40;
			int o = Math.floorDiv(i, 16);
			int p = Math.floorDiv(j, 40);
			int q = Math.floorDiv(k, 16);
			int r = p * 40 + 20;
			int s = 10;
			double d = this.fluidLevelSpreadNoise.sample(new DensityFunction.UnblendedNoisePos(o, p, q)) * 10.0;
			int t = MathHelper.roundDownToMultiple(d, 3);
			int u = r + t;
			return Math.min(l, u);
		}

		private BlockState method_42353(int i, int j, int k, AquiferSampler.FluidLevel fluidLevel, int l) {
			BlockState blockState = fluidLevel.state;
			if (l <= -10 && l != DimensionType.field_35479 && fluidLevel.state != Blocks.LAVA.getDefaultState()) {
				int m = 64;
				int n = 40;
				int o = Math.floorDiv(i, 64);
				int p = Math.floorDiv(j, 40);
				int q = Math.floorDiv(k, 64);
				double d = this.fluidTypeNoise.sample(new DensityFunction.UnblendedNoisePos(o, p, q));
				if (Math.abs(d) > 0.3) {
					blockState = Blocks.LAVA.getDefaultState();
				}
			}

			return blockState;
		}
	}
}
