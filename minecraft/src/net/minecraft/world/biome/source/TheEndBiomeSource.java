package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.StructureFeature;

public class TheEndBiomeSource extends BiomeSource {
	private final SimplexNoiseSampler noise;
	private final ChunkRandom random;
	private final Biome[] biomes = new Biome[]{Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS};

	public TheEndBiomeSource(TheEndBiomeSourceConfig config) {
		this.random = new ChunkRandom(config.getSeed());
		this.random.consume(17292);
		this.noise = new SimplexNoiseSampler(this.random);
	}

	@Override
	public Biome getBiome(int x, int z) {
		int i = x >> 4;
		int j = z >> 4;
		if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
			return Biomes.THE_END;
		} else {
			float f = this.method_8757(i * 2 + 1, j * 2 + 1);
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
	public Biome[] sampleBiomes(int x, int z, int width, int height, boolean bl) {
		Biome[] biomes = new Biome[width * height];
		Long2ObjectMap<Biome> long2ObjectMap = new Long2ObjectOpenHashMap<>();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int k = i + x;
				int l = j + z;
				long m = ChunkPos.toLong(k, l);
				Biome biome = long2ObjectMap.get(m);
				if (biome == null) {
					biome = this.getBiome(k, l);
					long2ObjectMap.put(m, biome);
				}

				biomes[i + j * width] = biome;
			}
		}

		return biomes;
	}

	@Override
	public Set<Biome> getBiomesInArea(int x, int z, int radius) {
		int i = x - radius >> 2;
		int j = z - radius >> 2;
		int k = x + radius >> 2;
		int l = z + radius >> 2;
		int m = k - i + 1;
		int n = l - j + 1;
		return Sets.<Biome>newHashSet(this.sampleBiomes(i, j, m, n));
	}

	@Nullable
	@Override
	public BlockPos locateBiome(int x, int z, int radius, List<Biome> biomes, Random random) {
		int i = x - radius >> 2;
		int j = z - radius >> 2;
		int k = x + radius >> 2;
		int l = z + radius >> 2;
		int m = k - i + 1;
		int n = l - j + 1;
		Biome[] biomes2 = this.sampleBiomes(i, j, m, n);
		BlockPos blockPos = null;
		int o = 0;

		for (int p = 0; p < m * n; p++) {
			int q = i + p % m << 2;
			int r = j + p / m << 2;
			if (biomes.contains(biomes2[p])) {
				if (blockPos == null || random.nextInt(o + 1) == 0) {
					blockPos = new BlockPos(q, 0, r);
				}

				o++;
			}
		}

		return blockPos;
	}

	@Override
	public float method_8757(int i, int j) {
		int k = i / 2;
		int l = j / 2;
		int m = i % 2;
		int n = j % 2;
		float f = 100.0F - MathHelper.sqrt((float)(i * i + j * j)) * 8.0F;
		f = MathHelper.clamp(f, -100.0F, 80.0F);

		for (int o = -12; o <= 12; o++) {
			for (int p = -12; p <= 12; p++) {
				long q = (long)(k + o);
				long r = (long)(l + p);
				if (q * q + r * r > 4096L && this.noise.sample((double)q, (double)r) < -0.9F) {
					float g = (MathHelper.abs((float)q) * 3439.0F + MathHelper.abs((float)r) * 147.0F) % 13.0F + 9.0F;
					float h = (float)(m - o * 2);
					float s = (float)(n - p * 2);
					float t = 100.0F - MathHelper.sqrt(h * h + s * s) * g;
					t = MathHelper.clamp(t, -100.0F, 80.0F);
					f = Math.max(f, t);
				}
			}
		}

		return f;
	}

	@Override
	public boolean hasStructureFeature(StructureFeature<?> feature) {
		return (Boolean)this.structureFeatures.computeIfAbsent(feature, structureFeature -> {
			for (Biome biome : this.biomes) {
				if (biome.hasStructureFeature(structureFeature)) {
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			for (Biome biome : this.biomes) {
				this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
			}
		}

		return this.topMaterials;
	}
}
