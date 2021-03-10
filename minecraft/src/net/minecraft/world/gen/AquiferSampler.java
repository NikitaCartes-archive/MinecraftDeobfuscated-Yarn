package net.minecraft.world.gen;

import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class AquiferSampler {
	private final DoublePerlinNoiseSampler edgeDensityNoise;
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
		DoublePerlinNoiseSampler edgeDensityNoise,
		DoublePerlinNoiseSampler waterLevelNoise,
		ChunkGeneratorSettings settings,
		NoiseColumnSampler columnSampler,
		int height
	) {
		this.edgeDensityNoise = edgeDensityNoise;
		this.waterLevelNoise = waterLevelNoise;
		this.settings = settings;
		this.columnSampler = columnSampler;
		ChunkPos chunkPos = new ChunkPos(x, z);
		this.startX = this.getLocalX(chunkPos.getStartX()) - 1;
		int i = this.getLocalX(chunkPos.getEndX()) + 1;
		this.sizeX = i - this.startX + 1;
		int j = settings.getGenerationShapeConfig().getMinimumY();
		this.startY = this.getLocalY(j) - 1;
		int k = this.getLocalY(j + height) + 1;
		int l = k - this.startY + 1;
		this.startZ = this.getLocalZ(chunkPos.getStartZ()) - 1;
		int m = this.getLocalZ(chunkPos.getEndZ()) + 1;
		this.sizeZ = m - this.startZ + 1;
		int n = this.sizeX * l * this.sizeZ;
		this.waterLevels = new int[n];
		Arrays.fill(this.waterLevels, Integer.MAX_VALUE);
		this.blockPositions = new long[n];
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
		double d = this.maxDistance(l, m);
		double e = this.maxDistance(l, n);
		double f = this.maxDistance(m, n);
		this.waterLevel = r;
		this.needsFluidTick = d > 0.0;
		if (this.waterLevel >= y && y <= 9) {
			this.densityAddition = 1.0;
		} else if (d > -1.0) {
			double g = 1.0 + (this.edgeDensityNoise.sample((double)x, (double)y, (double)z) + 0.1) / 4.0;
			double h = this.calculateDensity(y, g, r, s);
			double ah = this.calculateDensity(y, g, r, t);
			double ai = this.calculateDensity(y, g, s, t);
			double aj = Math.max(0.0, d);
			double ak = Math.max(0.0, e);
			double al = Math.max(0.0, f);
			double am = 2.0 * aj * Math.max(h, Math.max(ah * ak, ai * al));
			this.densityAddition = Math.max(0.0, am);
		} else {
			this.densityAddition = 0.0;
		}
	}

	private double maxDistance(int a, int b) {
		double d = 25.0;
		return 1.0 - (double)Math.abs(b - a) / 25.0;
	}

	private double calculateDensity(int y, double noise, int a, int b) {
		return 0.5 * (double)Math.abs(a - b) * noise - Math.abs(0.5 * (double)(a + b) - (double)y - 0.5);
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

	private int getWaterLevel(long pos) {
		int i = BlockPos.unpackLongX(pos);
		int j = BlockPos.unpackLongY(pos);
		int k = BlockPos.unpackLongZ(pos);
		int l = this.getLocalX(i);
		int m = this.getLocalY(j);
		int n = this.getLocalZ(k);
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
			int k = -10;
			int l = 40;
			double d = this.waterLevelNoise.sample((double)Math.floorDiv(x, 64), (double)Math.floorDiv(y, 40) / 1.4, (double)Math.floorDiv(z, 64)) * 30.0 + -10.0;
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
