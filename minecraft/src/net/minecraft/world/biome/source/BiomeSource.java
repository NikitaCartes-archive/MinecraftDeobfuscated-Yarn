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

	public List<Biome> method_28443() {
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
	public BlockPos locateBiome(int x, int y, int z, int radius, List<Biome> list, Random random) {
		return this.method_24385(x, y, z, radius, 1, list, random, false);
	}

	@Nullable
	public BlockPos method_24385(int i, int j, int k, int l, int m, List<Biome> list, Random random, boolean bl) {
		int n = i >> 2;
		int o = k >> 2;
		int p = l >> 2;
		int q = j >> 2;
		BlockPos blockPos = null;
		int r = 0;
		int s = bl ? 0 : p;
		int t = s;

		while (t <= p) {
			for (int u = -t; u <= t; u += m) {
				boolean bl2 = Math.abs(u) == t;

				for (int v = -t; v <= t; v += m) {
					if (bl) {
						boolean bl3 = Math.abs(v) == t;
						if (!bl3 && !bl2) {
							continue;
						}
					}

					int w = n + v;
					int x = o + u;
					if (list.contains(this.getBiomeForNoiseGen(w, q, x))) {
						if (blockPos == null || random.nextInt(r + 1) == 0) {
							blockPos = new BlockPos(w << 2, j, x << 2);
							if (bl) {
								return blockPos;
							}
						}

						r++;
					}
				}
			}

			t += m;
		}

		return blockPos;
	}

	public boolean hasStructureFeature(StructureFeature<?> feature) {
		return (Boolean)this.structureFeatures
			.computeIfAbsent(feature, structureFeature -> this.biomes.stream().anyMatch(biome -> biome.hasStructureFeature(structureFeature)));
	}

	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			for (Biome biome : this.biomes) {
				this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
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
