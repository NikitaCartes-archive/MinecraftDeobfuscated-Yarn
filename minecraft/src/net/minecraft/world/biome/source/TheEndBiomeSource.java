package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkRandom;

public class TheEndBiomeSource extends BiomeSource {
	private final SimplexNoiseSampler noise;
	private static final Set<Biome> BIOMES = ImmutableSet.of(
		Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS
	);

	public TheEndBiomeSource(long l) {
		super(BIOMES);
		ChunkRandom chunkRandom = new ChunkRandom(l);
		chunkRandom.consume(17292);
		this.noise = new SimplexNoiseSampler(chunkRandom);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource create(long seed) {
		return new TheEndBiomeSource(seed);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		int i = biomeX >> 2;
		int j = biomeZ >> 2;
		if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
			return Biomes.THE_END;
		} else {
			float f = this.getNoiseAt(i * 2 + 1, j * 2 + 1);
			if (f > 40.0F) {
				return Biomes.END_HIGHLANDS;
			} else if (f >= 0.0F) {
				return Biomes.END_MIDLANDS;
			} else {
				return f < -20.0F ? Biomes.SMALL_END_ISLANDS : Biomes.END_BARRENS;
			}
		}
	}

	@Override
	public float getNoiseAt(int x, int z) {
		int i = x / 2;
		int j = z / 2;
		int k = x % 2;
		int l = z % 2;
		float f = 100.0F - MathHelper.sqrt((float)(x * x + z * z)) * 8.0F;
		f = MathHelper.clamp(f, -100.0F, 80.0F);

		for (int m = -12; m <= 12; m++) {
			for (int n = -12; n <= 12; n++) {
				long o = (long)(i + m);
				long p = (long)(j + n);
				if (o * o + p * p > 4096L && this.noise.sample((double)o, (double)p) < -0.9F) {
					float g = (MathHelper.abs((float)o) * 3439.0F + MathHelper.abs((float)p) * 147.0F) % 13.0F + 9.0F;
					float h = (float)(k - m * 2);
					float q = (float)(l - n * 2);
					float r = 100.0F - MathHelper.sqrt(h * h + q * q) * g;
					r = MathHelper.clamp(r, -100.0F, 80.0F);
					f = Math.max(f, r);
				}
			}
		}

		return f;
	}
}
