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

	public TheEndBiomeSource(TheEndBiomeSourceConfig theEndBiomeSourceConfig) {
		this.random = new ChunkRandom(theEndBiomeSourceConfig.getSeed());
		this.random.consume(17292);
		this.noise = new SimplexNoiseSampler(this.random);
	}

	@Override
	public Biome getBiome(int i, int j) {
		int k = i >> 4;
		int l = j >> 4;
		if ((long)k * (long)k + (long)l * (long)l <= 4096L) {
			return Biomes.THE_END;
		} else {
			float f = this.method_8757(k * 2 + 1, l * 2 + 1);
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
	public Biome[] sampleBiomes(int i, int j, int k, int l, boolean bl) {
		Biome[] biomes = new Biome[k * l];
		Long2ObjectMap<Biome> long2ObjectMap = new Long2ObjectOpenHashMap<>();

		for (int m = 0; m < k; m++) {
			for (int n = 0; n < l; n++) {
				int o = m + i;
				int p = n + j;
				long q = ChunkPos.toLong(o, p);
				Biome biome = long2ObjectMap.get(q);
				if (biome == null) {
					biome = this.getBiome(o, p);
					long2ObjectMap.put(q, biome);
				}

				biomes[m + n * k] = biome;
			}
		}

		return biomes;
	}

	@Override
	public Set<Biome> getBiomesInArea(int i, int j, int k) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		return Sets.<Biome>newHashSet(this.sampleBiomes(l, m, p, q));
	}

	@Nullable
	@Override
	public BlockPos locateBiome(int i, int j, int k, List<Biome> list, Random random) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		Biome[] biomes = this.sampleBiomes(l, m, p, q);
		BlockPos blockPos = null;
		int r = 0;

		for (int s = 0; s < p * q; s++) {
			int t = l + s % p << 2;
			int u = m + s / p << 2;
			if (list.contains(biomes[s])) {
				if (blockPos == null || random.nextInt(r + 1) == 0) {
					blockPos = new BlockPos(t, 0, u);
				}

				r++;
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
	public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
		return (Boolean)this.structureFeatures.computeIfAbsent(structureFeature, structureFeaturex -> {
			for (Biome biome : this.biomes) {
				if (biome.hasStructureFeature(structureFeaturex)) {
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
