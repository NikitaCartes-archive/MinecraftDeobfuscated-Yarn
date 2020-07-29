package net.minecraft.world.biome.source;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class BiomeSource implements BiomeAccess.Storage {
	public static final Codec<BiomeSource> field_24713 = Registry.BIOME_SOURCE.dispatchStable(BiomeSource::method_28442, Function.identity());
	private static final List<Biome> SPAWN_BIOMES = Lists.<Biome>newArrayList(
		Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS
	);
	protected final Map<StructureFeature<?>, Boolean> structureFeatures = Maps.<StructureFeature<?>, Boolean>newHashMap();
	protected final Set<BlockState> topMaterials = Sets.<BlockState>newHashSet();
	protected final List<Biome> biomes;

	protected BiomeSource(List<Biome> biomes) {
		this.biomes = biomes;
	}

	protected abstract Codec<? extends BiomeSource> method_28442();

	@Environment(EnvType.CLIENT)
	public abstract BiomeSource withSeed(long seed);

	public List<Biome> getSpawnBiomes() {
		return SPAWN_BIOMES;
	}

	public List<Biome> getBiomes() {
		return this.biomes;
	}

	public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
		int i = x - radius >> 2;
		int j = y - radius >> 2;
		int k = z - radius >> 2;
		int l = x + radius >> 2;
		int m = y + radius >> 2;
		int n = z + radius >> 2;
		int o = l - i + 1;
		int p = m - j + 1;
		int q = n - k + 1;
		Set<Biome> set = Sets.<Biome>newHashSet();

		for (int r = 0; r < q; r++) {
			for (int s = 0; s < o; s++) {
				for (int t = 0; t < p; t++) {
					int u = i + s;
					int v = j + t;
					int w = k + r;
					set.add(this.getBiomeForNoiseGen(u, v, w));
				}
			}
		}

		return set;
	}

	@Nullable
	public BlockPos locateBiome(int x, int y, int z, int radius, List<Biome> biomes, Random random) {
		return this.locateBiome(x, y, z, radius, 1, biomes, random, false);
	}

	@Nullable
	public BlockPos locateBiome(int x, int y, int z, int radius, int i, List<Biome> biomes, Random random, boolean bl) {
		int j = x >> 2;
		int k = z >> 2;
		int l = radius >> 2;
		int m = y >> 2;
		BlockPos blockPos = null;
		int n = 0;
		int o = bl ? 0 : l;
		int p = o;

		while (p <= l) {
			for (int q = -p; q <= p; q += i) {
				boolean bl2 = Math.abs(q) == p;

				for (int r = -p; r <= p; r += i) {
					if (bl) {
						boolean bl3 = Math.abs(r) == p;
						if (!bl3 && !bl2) {
							continue;
						}
					}

					int s = j + r;
					int t = k + q;
					if (biomes.contains(this.getBiomeForNoiseGen(s, m, t))) {
						if (blockPos == null || random.nextInt(n + 1) == 0) {
							blockPos = new BlockPos(s << 2, y, t << 2);
							if (bl) {
								return blockPos;
							}
						}

						n++;
					}
				}
			}

			p += i;
		}

		return blockPos;
	}

	public boolean hasStructureFeature(StructureFeature<?> feature) {
		return (Boolean)this.structureFeatures
			.computeIfAbsent(feature, structureFeature -> this.biomes.stream().anyMatch(biome -> biome.getGenerationSettings().hasStructureFeature(structureFeature)));
	}

	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			for (Biome biome : this.biomes) {
				this.topMaterials.add(biome.getGenerationSettings().getSurfaceConfig().getTopMaterial());
			}
		}

		return this.topMaterials;
	}

	static {
		Registry.register(Registry.BIOME_SOURCE, "fixed", FixedBiomeSource.field_24717);
		Registry.register(Registry.BIOME_SOURCE, "multi_noise", MultiNoiseBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardBiomeSource.field_24715);
		Registry.register(Registry.BIOME_SOURCE, "vanilla_layered", VanillaLayeredBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "the_end", TheEndBiomeSource.field_24730);
	}
}
