package net.minecraft.world.biome.source;

import com.google.common.hash.Hashing;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class BiomeAccess {
	public static final int CHUNK_CENTER_OFFSET = BiomeCoords.fromBlock(8);
	private static final int field_34466 = 2;
	private static final int field_34467 = 4;
	private static final int field_34468 = 3;
	private final BiomeAccess.Storage storage;
	private final long seed;

	public BiomeAccess(BiomeAccess.Storage storage, long seed) {
		this.storage = storage;
		this.seed = seed;
	}

	public static long hashSeed(long seed) {
		return Hashing.sha256().hashLong(seed).asLong();
	}

	public BiomeAccess withSource(BiomeAccess.Storage storage) {
		return new BiomeAccess(storage, this.seed);
	}

	public RegistryEntry<Biome> getBiome(BlockPos pos) {
		int i = pos.getX() - 2;
		int j = pos.getY() - 2;
		int k = pos.getZ() - 2;
		int l = i >> 2;
		int m = j >> 2;
		int n = k >> 2;
		double d = (double)(i & 3) / 4.0;
		double e = (double)(j & 3) / 4.0;
		double f = (double)(k & 3) / 4.0;
		int o = 0;
		double g = Double.POSITIVE_INFINITY;

		for (int p = 0; p < 8; p++) {
			boolean bl = (p & 4) == 0;
			boolean bl2 = (p & 2) == 0;
			boolean bl3 = (p & 1) == 0;
			int q = bl ? l : l + 1;
			int r = bl2 ? m : m + 1;
			int s = bl3 ? n : n + 1;
			double h = bl ? d : d - 1.0;
			double t = bl2 ? e : e - 1.0;
			double u = bl3 ? f : f - 1.0;
			double v = method_38106(this.seed, q, r, s, h, t, u);
			if (g > v) {
				o = p;
				g = v;
			}
		}

		int px = (o & 4) == 0 ? l : l + 1;
		int w = (o & 2) == 0 ? m : m + 1;
		int x = (o & 1) == 0 ? n : n + 1;
		return this.storage.getBiomeForNoiseGen(px, w, x);
	}

	public RegistryEntry<Biome> getBiomeForNoiseGen(double x, double y, double z) {
		int i = BiomeCoords.fromBlock(MathHelper.floor(x));
		int j = BiomeCoords.fromBlock(MathHelper.floor(y));
		int k = BiomeCoords.fromBlock(MathHelper.floor(z));
		return this.getBiomeForNoiseGen(i, j, k);
	}

	public RegistryEntry<Biome> getBiomeForNoiseGen(BlockPos pos) {
		int i = BiomeCoords.fromBlock(pos.getX());
		int j = BiomeCoords.fromBlock(pos.getY());
		int k = BiomeCoords.fromBlock(pos.getZ());
		return this.getBiomeForNoiseGen(i, j, k);
	}

	public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.storage.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
	}

	private static double method_38106(long l, int i, int j, int k, double d, double e, double f) {
		long m = SeedMixer.mixSeed(l, (long)i);
		m = SeedMixer.mixSeed(m, (long)j);
		m = SeedMixer.mixSeed(m, (long)k);
		m = SeedMixer.mixSeed(m, (long)i);
		m = SeedMixer.mixSeed(m, (long)j);
		m = SeedMixer.mixSeed(m, (long)k);
		double g = method_38108(m);
		m = SeedMixer.mixSeed(m, l);
		double h = method_38108(m);
		m = SeedMixer.mixSeed(m, l);
		double n = method_38108(m);
		return MathHelper.square(f + n) + MathHelper.square(e + h) + MathHelper.square(d + g);
	}

	private static double method_38108(long l) {
		double d = (double)Math.floorMod(l >> 24, 1024) / 1024.0;
		return (d - 0.5) * 0.9;
	}

	public interface Storage {
		RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ);
	}
}
