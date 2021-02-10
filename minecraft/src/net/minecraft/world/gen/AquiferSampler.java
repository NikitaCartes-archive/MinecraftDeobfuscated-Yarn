package net.minecraft.world.gen;

import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class AquiferSampler {
	private final DoublePerlinNoiseSampler field_28813;
	private final DoublePerlinNoiseSampler waterLevelNoise;
	private final ChunkGeneratorSettings settings;
	private final int[] waterLevels;
	private final long[] blockPositions;
	private double densityAddition;
	private int waterLevel;
	private boolean needsFluidTick;
	private final NoiseColumnSampler columnSampler;
	private final int startX;
	private final int startY;
	private final int startZ;
	private final int sizeX;
	private final int sizeZ;

	public AquiferSampler(
		int x,
		int z,
		DoublePerlinNoiseSampler doublePerlinNoiseSampler,
		DoublePerlinNoiseSampler waterLevelNoise,
		ChunkGeneratorSettings settings,
		NoiseColumnSampler columnSampler,
		int i
	) {
		this.field_28813 = doublePerlinNoiseSampler;
		this.waterLevelNoise = waterLevelNoise;
		this.settings = settings;
		this.columnSampler = columnSampler;
		ChunkPos chunkPos = new ChunkPos(x, z);
		this.startX = this.method_33734(chunkPos.getStartX()) - 1;
		int j = this.method_33734(chunkPos.getEndX()) + 1;
		this.sizeX = j - this.startX + 1;
		int k = settings.getGenerationShapeConfig().getMinimumY();
		this.startY = this.method_33740(k) - 1;
		int l = this.method_33740(k + i) + 1;
		int m = l - this.startY + 1;
		this.startZ = this.method_33743(chunkPos.getStartZ()) - 1;
		int n = this.method_33743(chunkPos.getEndZ()) + 1;
		this.sizeZ = n - this.startZ + 1;
		int o = this.sizeX * m * this.sizeZ;
		this.waterLevels = new int[o];
		Arrays.fill(this.waterLevels, Integer.MAX_VALUE);
		this.blockPositions = new long[o];
		Arrays.fill(this.blockPositions, Long.MAX_VALUE);
	}

	private int index(int x, int y, int z) {
		int i = x - this.startX;
		int j = y - this.startY;
		int k = z - this.startZ;
		return (j * this.sizeZ + k) * this.sizeX + i;
	}

	public void apply(int x, int y, int z) {
		int i = Math.floorDiv(x - 5, 16);
		int j = Math.floorDiv(y + 1, 12);
		int k = Math.floorDiv(z - 5, 16);
		int l = Integer.MAX_VALUE;
		int m = Integer.MAX_VALUE;
		int n = Integer.MAX_VALUE;
		long o = 0L;
		long p = 0L;
		long q = 0L;

		for (int r = 0; r <= 1; r++) {
			for (int s = -1; s <= 1; s++) {
				for (int t = 0; t <= 1; t++) {
					int u = i + r;
					int v = j + s;
					int w = k + t;
					int aa = this.index(u, v, w);
					long ab = this.blockPositions[aa];
					long ac;
					if (ab != Long.MAX_VALUE) {
						ac = ab;
					} else {
						ChunkRandom chunkRandom = new ChunkRandom(MathHelper.hashCode(u, v * 3, w) + 1L);
						ac = BlockPos.asLong(u * 16 + chunkRandom.nextInt(10), v * 12 + chunkRandom.nextInt(9), w * 16 + chunkRandom.nextInt(10));
						this.blockPositions[aa] = ac;
					}

					int ad = BlockPos.unpackLongX(ac) - x;
					int ae = BlockPos.unpackLongY(ac) - y;
					int af = BlockPos.unpackLongZ(ac) - z;
					int ag = ad * ad + ae * ae + af * af;
					if (l >= ag) {
						q = p;
						p = o;
						o = ac;
						n = m;
						m = l;
						l = ag;
					} else if (m >= ag) {
						q = p;
						p = ac;
						n = m;
						m = ag;
					} else if (n >= ag) {
						q = ac;
						n = ag;
					}
				}
			}
		}

		int r = this.getWaterLevel(o);
		int s = this.getWaterLevel(p);
		int t = this.getWaterLevel(q);
		double d = this.method_33736(l, m);
		double e = this.method_33736(l, n);
		double f = this.method_33736(m, n);
		this.waterLevel = r;
		this.needsFluidTick = d > 0.0;
		if (d > -1.0) {
			double g = 1.0 + (this.field_28813.sample((double)x, (double)y, (double)z) + 0.1) / 4.0;
			double h = this.method_33735(y, g, r, s);
			double ah = this.method_33735(y, g, r, t);
			double ai = this.method_33735(y, g, s, t);
			double aj = Math.max(0.0, d);
			double ak = Math.max(0.0, e);
			double al = Math.max(0.0, f);
			double am = 2.0 * aj * Math.max(h, Math.max(ah * ak, ai * al));
			this.densityAddition = Math.max(0.0, am);
		} else {
			this.densityAddition = 0.0;
		}
	}

	private double method_33736(int i, int j) {
		double d = 25.0;
		return 1.0 - (double)Math.abs(j - i) / 25.0;
	}

	private double method_33735(int i, double d, int j, int k) {
		return 0.5 * (double)Math.abs(j - k) * d - Math.abs(0.5 * (double)(j + k) - (double)i - 0.5);
	}

	private int method_33734(int i) {
		return Math.floorDiv(i, 16);
	}

	private int method_33740(int i) {
		return Math.floorDiv(i, 12);
	}

	private int method_33743(int i) {
		return Math.floorDiv(i, 16);
	}

	private int getWaterLevel(long pos) {
		int i = BlockPos.unpackLongX(pos);
		int j = BlockPos.unpackLongY(pos);
		int k = BlockPos.unpackLongZ(pos);
		int l = this.method_33734(i);
		int m = this.method_33740(j);
		int n = this.method_33743(k);
		int o = this.index(l, m, n);
		int p = this.waterLevels[o];
		if (p != Integer.MAX_VALUE) {
			return p;
		} else {
			int q = this.getWaterLevel(i, j, k);
			this.waterLevels[o] = q;
			return q;
		}
	}

	private int getWaterLevel(int x, int y, int z) {
		int i = this.settings.getSeaLevel();
		if (y > 30) {
			return i;
		} else {
			int j = 64;
			int k = -8;
			int l = 40;
			double d = this.waterLevelNoise.sample((double)Math.floorDiv(x, 64), (double)Math.floorDiv(y, 40) / 1.4, (double)Math.floorDiv(z, 64)) * 30.0 + -8.0;
			if (Math.abs(d) > 8.0) {
				d *= 4.0;
			}

			int m = Math.floorDiv(y, 40) * 40 + 20;
			int n = m + MathHelper.floor(d);
			return Math.min(56, n);
		}
	}

	public int getWaterLevel() {
		return this.waterLevel;
	}

	public double getDensityAddition() {
		return this.densityAddition;
	}

	public boolean needsFluidTick() {
		return this.needsFluidTick;
	}
}
