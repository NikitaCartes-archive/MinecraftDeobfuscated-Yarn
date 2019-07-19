package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.LevelProperties;

public class VanillaLayeredBiomeSource extends BiomeSource {
	private final BiomeLayerSampler biomeSampler;
	private final BiomeLayerSampler biomeLayer;
	private final Biome[] biomes = new Biome[]{
		Biomes.OCEAN,
		Biomes.PLAINS,
		Biomes.DESERT,
		Biomes.MOUNTAINS,
		Biomes.FOREST,
		Biomes.TAIGA,
		Biomes.SWAMP,
		Biomes.RIVER,
		Biomes.FROZEN_OCEAN,
		Biomes.FROZEN_RIVER,
		Biomes.SNOWY_TUNDRA,
		Biomes.SNOWY_MOUNTAINS,
		Biomes.MUSHROOM_FIELDS,
		Biomes.MUSHROOM_FIELD_SHORE,
		Biomes.BEACH,
		Biomes.DESERT_HILLS,
		Biomes.WOODED_HILLS,
		Biomes.TAIGA_HILLS,
		Biomes.MOUNTAIN_EDGE,
		Biomes.JUNGLE,
		Biomes.JUNGLE_HILLS,
		Biomes.JUNGLE_EDGE,
		Biomes.DEEP_OCEAN,
		Biomes.STONE_SHORE,
		Biomes.SNOWY_BEACH,
		Biomes.BIRCH_FOREST,
		Biomes.BIRCH_FOREST_HILLS,
		Biomes.DARK_FOREST,
		Biomes.SNOWY_TAIGA,
		Biomes.SNOWY_TAIGA_HILLS,
		Biomes.GIANT_TREE_TAIGA,
		Biomes.GIANT_TREE_TAIGA_HILLS,
		Biomes.WOODED_MOUNTAINS,
		Biomes.SAVANNA,
		Biomes.SAVANNA_PLATEAU,
		Biomes.BADLANDS,
		Biomes.WOODED_BADLANDS_PLATEAU,
		Biomes.BADLANDS_PLATEAU,
		Biomes.WARM_OCEAN,
		Biomes.LUKEWARM_OCEAN,
		Biomes.COLD_OCEAN,
		Biomes.DEEP_WARM_OCEAN,
		Biomes.DEEP_LUKEWARM_OCEAN,
		Biomes.DEEP_COLD_OCEAN,
		Biomes.DEEP_FROZEN_OCEAN,
		Biomes.SUNFLOWER_PLAINS,
		Biomes.DESERT_LAKES,
		Biomes.GRAVELLY_MOUNTAINS,
		Biomes.FLOWER_FOREST,
		Biomes.TAIGA_MOUNTAINS,
		Biomes.SWAMP_HILLS,
		Biomes.ICE_SPIKES,
		Biomes.MODIFIED_JUNGLE,
		Biomes.MODIFIED_JUNGLE_EDGE,
		Biomes.TALL_BIRCH_FOREST,
		Biomes.TALL_BIRCH_HILLS,
		Biomes.DARK_FOREST_HILLS,
		Biomes.SNOWY_TAIGA_MOUNTAINS,
		Biomes.GIANT_SPRUCE_TAIGA,
		Biomes.GIANT_SPRUCE_TAIGA_HILLS,
		Biomes.MODIFIED_GRAVELLY_MOUNTAINS,
		Biomes.SHATTERED_SAVANNA,
		Biomes.SHATTERED_SAVANNA_PLATEAU,
		Biomes.ERODED_BADLANDS,
		Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU,
		Biomes.MODIFIED_BADLANDS_PLATEAU
	};

	public VanillaLayeredBiomeSource(VanillaLayeredBiomeSourceConfig config) {
		LevelProperties levelProperties = config.getLevelProperties();
		OverworldChunkGeneratorConfig overworldChunkGeneratorConfig = config.getGeneratorSettings();
		BiomeLayerSampler[] biomeLayerSamplers = BiomeLayers.build(levelProperties.getSeed(), levelProperties.getGeneratorType(), overworldChunkGeneratorConfig);
		this.biomeSampler = biomeLayerSamplers[0];
		this.biomeLayer = biomeLayerSamplers[1];
	}

	@Override
	public Biome getBiome(int x, int z) {
		return this.biomeLayer.sample(x, z);
	}

	@Override
	public Biome getBiomeForNoiseGen(int x, int z) {
		return this.biomeSampler.sample(x, z);
	}

	@Override
	public Biome[] sampleBiomes(int x, int z, int width, int height, boolean bl) {
		return this.biomeLayer.sample(x, z, width, height);
	}

	@Override
	public Set<Biome> getBiomesInArea(int x, int z, int radius) {
		int i = x - radius >> 2;
		int j = z - radius >> 2;
		int k = x + radius >> 2;
		int l = z + radius >> 2;
		int m = k - i + 1;
		int n = l - j + 1;
		Set<Biome> set = Sets.<Biome>newHashSet();
		Collections.addAll(set, this.biomeSampler.sample(i, j, m, n));
		return set;
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
		Biome[] biomes2 = this.biomeSampler.sample(i, j, m, n);
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
