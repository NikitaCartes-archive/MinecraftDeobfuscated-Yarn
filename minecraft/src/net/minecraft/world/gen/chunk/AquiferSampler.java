package net.minecraft.world.gen.chunk;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.class_6568;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.BlockPosRandomDeriver;

public interface AquiferSampler {
	static AquiferSampler aquifer(
		class_6568 arg,
		ChunkPos chunkPos,
		DoublePerlinNoiseSampler doublePerlinNoiseSampler,
		DoublePerlinNoiseSampler doublePerlinNoiseSampler2,
		DoublePerlinNoiseSampler doublePerlinNoiseSampler3,
		BlockPosRandomDeriver blockPosRandomDeriver,
		NoiseColumnSampler noiseColumnSampler,
		int i,
		int j,
		AquiferSampler.class_6565 arg2
	) {
		return new AquiferSampler.Impl(
			arg, chunkPos, doublePerlinNoiseSampler, doublePerlinNoiseSampler2, doublePerlinNoiseSampler3, blockPosRandomDeriver, noiseColumnSampler, i, j, arg2
		);
	}

	static AquiferSampler seaLevel(AquiferSampler.class_6565 arg) {
		return new AquiferSampler() {
			@Nullable
			@Override
			public BlockState apply(int i, int x, int y, double d, double e) {
				return e > 0.0 ? null : arg.getFluidLevel(i, x, y).method_38318(x);
			}

			@Override
			public boolean needsFluidTick() {
				return false;
			}
		};
	}

	@Nullable
	BlockState apply(int i, int x, int y, double d, double e);

	boolean needsFluidTick();

	public static final class FluidLevel {
		final int y;
		final BlockState state;

		public FluidLevel(int y, BlockState state) {
			this.y = y;
			this.state = state;
		}

		public BlockState method_38318(int i) {
			return i < this.y ? this.state : Blocks.AIR.getDefaultState();
		}
	}

	public static class Impl implements AquiferSampler, AquiferSampler.class_6565 {
		private static final int field_31451 = 10;
		private static final int field_31452 = 9;
		private static final int field_31453 = 10;
		private static final int field_31454 = 6;
		private static final int field_31455 = 3;
		private static final int field_31456 = 6;
		private static final int field_31457 = 16;
		private static final int field_31458 = 12;
		private static final int field_31459 = 16;
		private final class_6568 field_34578;
		private final DoublePerlinNoiseSampler edgeDensityNoise;
		private final DoublePerlinNoiseSampler fluidLevelNoise;
		private final DoublePerlinNoiseSampler fluidTypeNoise;
		private final BlockPosRandomDeriver field_34579;
		private final AquiferSampler.FluidLevel[] waterLevels;
		private final long[] blockPositions;
		private final AquiferSampler.class_6565 field_34580;
		private boolean needsFluidTick;
		private final NoiseColumnSampler columnSampler;
		private final int startX;
		private final int startY;
		private final int startZ;
		private final int sizeX;
		private final int sizeZ;
		private static final int[][] field_34581 = new int[][]{
			{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {3, 0}, {-3, 0}, {0, 3}, {0, -3}, {2, 2}, {2, -2}, {-2, 2}, {-2, 2}
		};

		Impl(
			class_6568 arg,
			ChunkPos chunkPos,
			DoublePerlinNoiseSampler doublePerlinNoiseSampler,
			DoublePerlinNoiseSampler doublePerlinNoiseSampler2,
			DoublePerlinNoiseSampler doublePerlinNoiseSampler3,
			BlockPosRandomDeriver blockPosRandomDeriver,
			NoiseColumnSampler noiseColumnSampler,
			int i,
			int j,
			AquiferSampler.class_6565 arg2
		) {
			this.field_34578 = arg;
			this.edgeDensityNoise = doublePerlinNoiseSampler;
			this.fluidLevelNoise = doublePerlinNoiseSampler2;
			this.fluidTypeNoise = doublePerlinNoiseSampler3;
			this.field_34579 = blockPosRandomDeriver;
			this.columnSampler = noiseColumnSampler;
			this.startX = this.getLocalX(chunkPos.getStartX()) - 1;
			this.field_34580 = arg2;
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
		public BlockState apply(int i, int x, int y, double d, double e) {
			if (d <= -64.0) {
				return this.field_34580.getFluidLevel(i, x, y).method_38318(x);
			} else {
				if (e <= 0.0) {
					AquiferSampler.FluidLevel fluidLevel = this.field_34580.getFluidLevel(i, x, y);
					double f;
					BlockState blockState;
					boolean bl;
					if (fluidLevel.method_38318(x).isOf(Blocks.LAVA)) {
						blockState = Blocks.LAVA.getDefaultState();
						f = 0.0;
						bl = false;
					} else {
						int j = Math.floorDiv(i - 5, 16);
						int k = Math.floorDiv(x + 1, 12);
						int l = Math.floorDiv(y - 5, 16);
						int m = Integer.MAX_VALUE;
						int n = Integer.MAX_VALUE;
						int o = Integer.MAX_VALUE;
						long p = 0L;
						long q = 0L;
						long r = 0L;

						for (int s = 0; s <= 1; s++) {
							for (int t = -1; t <= 1; t++) {
								for (int u = 0; u <= 1; u++) {
									int v = j + s;
									int w = k + t;
									int z = l + u;
									int aa = this.index(v, w, z);
									long ab = this.blockPositions[aa];
									long ac;
									if (ab != Long.MAX_VALUE) {
										ac = ab;
									} else {
										AbstractRandom abstractRandom = this.field_34579.createRandom(v, w, z);
										ac = BlockPos.asLong(v * 16 + abstractRandom.nextInt(10), w * 12 + abstractRandom.nextInt(9), z * 16 + abstractRandom.nextInt(10));
										this.blockPositions[aa] = ac;
									}

									int ad = BlockPos.unpackLongX(ac) - i;
									int ae = BlockPos.unpackLongY(ac) - x;
									int af = BlockPos.unpackLongZ(ac) - y;
									int ag = ad * ad + ae * ae + af * af;
									if (m >= ag) {
										r = q;
										q = p;
										p = ac;
										o = n;
										n = m;
										m = ag;
									} else if (n >= ag) {
										r = q;
										q = ac;
										o = n;
										n = ag;
									} else if (o >= ag) {
										r = ac;
										o = ag;
									}
								}
							}
						}

						AquiferSampler.FluidLevel fluidLevel2 = this.getWaterLevel(p);
						AquiferSampler.FluidLevel fluidLevel3 = this.getWaterLevel(q);
						AquiferSampler.FluidLevel fluidLevel4 = this.getWaterLevel(r);
						double g = this.maxDistance(m, n);
						double h = this.maxDistance(m, o);
						double ah = this.maxDistance(n, o);
						bl = g > 0.0;
						if (fluidLevel2.method_38318(x).isOf(Blocks.WATER) && this.field_34580.getFluidLevel(i, x - 1, y).method_38318(x - 1).isOf(Blocks.LAVA)) {
							f = 1.0;
						} else if (g > -1.0) {
							double ai = 1.0 + (this.edgeDensityNoise.sample((double)i, (double)x, (double)y) + 0.05) / 4.0;
							double aj = this.calculateDensity(x, ai, fluidLevel2, fluidLevel3);
							double ak = this.calculateDensity(x, ai, fluidLevel2, fluidLevel4);
							double al = this.calculateDensity(x, ai, fluidLevel3, fluidLevel4);
							double am = Math.max(0.0, g);
							double an = Math.max(0.0, h);
							double ao = Math.max(0.0, ah);
							double ap = 2.0 * am * Math.max(aj, Math.max(ak * an, al * ao));
							float aq = 0.5F;
							if (x <= fluidLevel2.y
								&& x <= fluidLevel3.y
								&& fluidLevel2.y != fluidLevel3.y
								&& Math.abs(this.edgeDensityNoise.sample((double)((float)i * 0.5F), (double)((float)x * 0.5F), (double)((float)y * 0.5F))) < 0.3) {
								f = 1.0;
							} else {
								f = Math.max(0.0, ap);
							}
						} else {
							f = 0.0;
						}

						blockState = fluidLevel2.method_38318(x);
					}

					if (e + f <= 0.0) {
						this.needsFluidTick = bl;
						return blockState;
					}
				}

				this.needsFluidTick = false;
				return null;
			}
		}

		@Override
		public boolean needsFluidTick() {
			return this.needsFluidTick;
		}

		private double maxDistance(int a, int b) {
			double d = 25.0;
			return 1.0 - (double)Math.abs(b - a) / 25.0;
		}

		private double calculateDensity(int y, double noise, AquiferSampler.FluidLevel first, AquiferSampler.FluidLevel second) {
			BlockState blockState = first.method_38318(y);
			BlockState blockState2 = second.method_38318(y);
			if ((!blockState.isOf(Blocks.LAVA) || !blockState2.isOf(Blocks.WATER)) && (!blockState.isOf(Blocks.WATER) || !blockState2.isOf(Blocks.LAVA))) {
				int i = Math.abs(first.y - second.y);
				double d = 0.5 * (double)(first.y + second.y);
				double e = Math.abs(d - (double)y - 0.5);
				return 0.5 * (double)i * noise - e;
			} else {
				return 1.0;
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
				AquiferSampler.FluidLevel fluidLevel2 = this.getFluidLevel(i, j, k);
				this.waterLevels[o] = fluidLevel2;
				return fluidLevel2;
			}
		}

		@Override
		public AquiferSampler.FluidLevel getFluidLevel(int i, int y, int j) {
			AquiferSampler.FluidLevel fluidLevel = this.field_34580.getFluidLevel(i, y, j);
			int k = Integer.MAX_VALUE;
			int l = y + 6;
			boolean bl = false;

			for (int[] is : field_34581) {
				int m = i + ChunkSectionPos.getBlockCoord(is[0]);
				int n = j + ChunkSectionPos.getBlockCoord(is[1]);
				int o = this.columnSampler.method_38383(m, n, this.field_34578.method_38353(this.columnSampler, BiomeCoords.fromBlock(m), BiomeCoords.fromBlock(n)));
				int p = o + 8;
				boolean bl2 = is[0] == 0 && is[1] == 0;
				boolean bl3 = l > p;
				if (bl3 || bl2) {
					AquiferSampler.FluidLevel fluidLevel2 = this.field_34580.getFluidLevel(m, p, n);
					if (!fluidLevel2.method_38318(p).isAir()) {
						if (bl2) {
							bl = true;
						}

						if (bl3) {
							return fluidLevel2;
						}
					}
				}

				k = Math.min(k, o);
			}

			int q = y - 6;
			if (q > k) {
				return fluidLevel;
			} else {
				int r = 40;
				int s = Math.floorDiv(i, 64);
				int t = Math.floorDiv(y, 40);
				int m = Math.floorDiv(j, 64);
				int n = -20;
				int o = 50;
				double d = this.fluidLevelNoise.sample((double)s, (double)t / 1.4, (double)m) * 50.0 + -20.0;
				int u = t * 40 + 20;
				if (bl && u >= k - 30 && u < fluidLevel.y) {
					if (d > -12.0) {
						return fluidLevel;
					}

					if (d > -20.0) {
						return new AquiferSampler.FluidLevel(k - 12 + (int)d, Blocks.WATER.getDefaultState());
					}

					d = -40.0;
				} else {
					if (d > 4.0) {
						d *= 4.0;
					}

					if (d < -10.0) {
						d = -40.0;
					}
				}

				int v = u + MathHelper.floor(d);
				int w = Math.min(k, v);
				boolean bl4 = false;
				if (u == -20 && !bl) {
					double e = this.fluidTypeNoise.sample((double)s, (double)t / 1.4, (double)m);
					bl4 = Math.abs(e) > 0.22F;
				}

				return new AquiferSampler.FluidLevel(w, bl4 ? Blocks.LAVA.getDefaultState() : fluidLevel.state);
			}
		}
	}

	public interface class_6565 {
		AquiferSampler.FluidLevel getFluidLevel(int i, int j, int k);
	}
}
