package net.minecraft.world.gen.chunk;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.noise.NoiseType;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import org.apache.commons.lang3.mutable.MutableDouble;

public interface AquiferSampler {
	static AquiferSampler aquifer(
		ChunkNoiseSampler chunkNoiseSampler,
		ChunkPos chunkPos,
		NoiseType noiseType,
		NoiseType noiseType2,
		NoiseType noiseType3,
		NoiseType noiseType4,
		RandomDeriver randomDeriver,
		int minY,
		int height,
		AquiferSampler.FluidLevelSampler fluidLevelSampler
	) {
		return new AquiferSampler.Impl(chunkNoiseSampler, chunkPos, noiseType, noiseType2, noiseType3, noiseType4, randomDeriver, minY, height, fluidLevelSampler);
	}

	static AquiferSampler seaLevel(AquiferSampler.FluidLevelSampler fluidLevelSampler) {
		return new AquiferSampler() {
			@Nullable
			@Override
			public BlockState apply(NoiseType.NoisePos noisePos, double d) {
				return d > 0.0 ? null : fluidLevelSampler.getFluidLevel(noisePos.blockX(), noisePos.blockY(), noisePos.blockZ()).getBlockState(noisePos.blockY());
			}

			@Override
			public boolean needsFluidTick() {
				return false;
			}
		};
	}

	@Nullable
	BlockState apply(NoiseType.NoisePos noisePos, double d);

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
		private final NoiseType barrierNoise;
		private final NoiseType fluidLevelFloodednessNoise;
		private final NoiseType fluidLevelSpreadNoise;
		private final NoiseType fluidTypeNoise;
		private final RandomDeriver randomDeriver;
		private final AquiferSampler.FluidLevel[] waterLevels;
		private final long[] blockPositions;
		private final AquiferSampler.FluidLevelSampler fluidLevelSampler;
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
			NoiseType noiseType,
			NoiseType noiseType2,
			NoiseType noiseType3,
			NoiseType noiseType4,
			RandomDeriver randomDeriver,
			int minY,
			int height,
			AquiferSampler.FluidLevelSampler fluidLevelSampler
		) {
			this.chunkNoiseSampler = chunkNoiseSampler;
			this.barrierNoise = noiseType;
			this.fluidLevelFloodednessNoise = noiseType2;
			this.fluidLevelSpreadNoise = noiseType3;
			this.fluidTypeNoise = noiseType4;
			this.randomDeriver = randomDeriver;
			this.startX = this.getLocalX(chunkPos.getStartX()) - 1;
			this.fluidLevelSampler = fluidLevelSampler;
			int i = this.getLocalX(chunkPos.getEndX()) + 1;
			this.sizeX = i - this.startX + 1;
			this.startY = this.getLocalY(minY) - 1;
			int j = this.getLocalY(minY + height) + 1;
			int k = j - this.startY + 1;
			this.startZ = this.getLocalZ(chunkPos.getStartZ()) - 1;
			int l = this.getLocalZ(chunkPos.getEndZ()) + 1;
			this.sizeZ = l - this.startZ + 1;
			int m = this.sizeX * k * this.sizeZ;
			this.waterLevels = new AquiferSampler.FluidLevel[m];
			this.blockPositions = new long[m];
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
		public BlockState apply(NoiseType.NoisePos noisePos, double d) {
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
									AbstractRandom abstractRandom = this.randomDeriver.createRandom(x, y, z);
									ac = BlockPos.asLong(x * 16 + abstractRandom.nextInt(10), y * 12 + abstractRandom.nextInt(9), z * 16 + abstractRandom.nextInt(10));
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
			NoiseType.NoisePos noisePos, MutableDouble mutableDouble, AquiferSampler.FluidLevel fluidLevel, AquiferSampler.FluidLevel fluidLevel2
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

			int s = l + 8 - j;
			int t = 64;
			double d = bl ? MathHelper.clampedLerpFromProgress((double)s, 0.0, 64.0, 1.0, 0.0) : 0.0;
			double e = MathHelper.clamp(this.fluidLevelFloodednessNoise.sample(new NoiseType.UnblendedNoisePos(i, j, k)), -1.0, 1.0);
			double f = MathHelper.lerpFromProgress(d, 1.0, 0.0, -0.3, 0.8);
			if (e > f) {
				return fluidLevel;
			} else {
				double g = MathHelper.lerpFromProgress(d, 1.0, 0.0, -0.8, 0.4);
				if (e <= g) {
					return new AquiferSampler.FluidLevel(DimensionType.field_35479, fluidLevel.state);
				} else {
					int u = 16;
					int v = 40;
					int w = Math.floorDiv(i, 16);
					int x = Math.floorDiv(j, 40);
					int y = Math.floorDiv(k, 16);
					int z = x * 40 + 20;
					int aa = 10;
					double h = this.fluidLevelSpreadNoise.sample(new NoiseType.UnblendedNoisePos(w, x, y)) * 10.0;
					int ab = MathHelper.roundDownToMultiple(h, 3);
					int ac = z + ab;
					int ad = Math.min(l, ac);
					if (ac <= -10) {
						int ae = 64;
						int af = 40;
						int ag = Math.floorDiv(i, 64);
						int ah = Math.floorDiv(j, 40);
						int ai = Math.floorDiv(k, 64);
						double aj = this.fluidTypeNoise.sample(new NoiseType.UnblendedNoisePos(ag, ah, ai));
						if (Math.abs(aj) > 0.3) {
							return new AquiferSampler.FluidLevel(ad, Blocks.LAVA.getDefaultState());
						}
					}

					return new AquiferSampler.FluidLevel(ad, fluidLevel.state);
				}
			}
		}
	}
}
