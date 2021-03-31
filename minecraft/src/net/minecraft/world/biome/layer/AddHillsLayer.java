package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum AddHillsLayer implements MergingLayer, NorthWestCoordinateTransformer {
	INSTANCE;

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Int2IntMap MUTATED_BIOMES = Util.make(new Int2IntOpenHashMap(), map -> {
		map.put(1, 129);
		map.put(2, 130);
		map.put(3, 131);
		map.put(4, 132);
		map.put(5, 133);
		map.put(6, 134);
		map.put(12, 140);
		map.put(21, 149);
		map.put(23, 151);
		map.put(27, 155);
		map.put(28, 156);
		map.put(29, 157);
		map.put(30, 158);
		map.put(32, 160);
		map.put(33, 161);
		map.put(34, 162);
		map.put(35, 163);
		map.put(36, 164);
		map.put(37, 165);
		map.put(38, 166);
		map.put(39, 167);
	});

	@Override
	public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
		int i = sampler1.sample(this.transformX(x + 1), this.transformZ(z + 1));
		int j = sampler2.sample(this.transformX(x + 1), this.transformZ(z + 1));
		if (i > 255) {
			LOGGER.debug("old! {}", i);
		}

		int k = (j - BiomeIds.DESERT) % BiomeIds.DARK_FOREST;
		if (!BiomeLayers.isShallowOcean(i) && j >= BiomeIds.DESERT && k == BiomeIds.PLAINS) {
			return MUTATED_BIOMES.getOrDefault(i, i);
		} else {
			if (context.nextInt(3) == 0 || k == 0) {
				int l = i;
				if (i == BiomeIds.DESERT) {
					l = BiomeIds.DESERT_HILLS;
				} else if (i == BiomeIds.FOREST) {
					l = BiomeIds.WOODED_HILLS;
				} else if (i == BiomeIds.BIRCH_FOREST) {
					l = BiomeIds.BIRCH_FOREST_HILLS;
				} else if (i == BiomeIds.DARK_FOREST) {
					l = BiomeIds.PLAINS;
				} else if (i == BiomeIds.TAIGA) {
					l = BiomeIds.TAIGA_HILLS;
				} else if (i == BiomeIds.GIANT_TREE_TAIGA) {
					l = BiomeIds.GIANT_TREE_TAIGA_HILLS;
				} else if (i == BiomeIds.SNOWY_TAIGA) {
					l = BiomeIds.SNOWY_TAIGA_HILLS;
				} else if (i == BiomeIds.PLAINS) {
					l = context.nextInt(3) == 0 ? BiomeIds.WOODED_HILLS : BiomeIds.FOREST;
				} else if (i == BiomeIds.SNOWY_TUNDRA) {
					l = BiomeIds.SNOWY_MOUNTAINS;
				} else if (i == BiomeIds.JUNGLE) {
					l = BiomeIds.JUNGLE_HILLS;
				} else if (i == BiomeIds.BAMBOO_JUNGLE) {
					l = BiomeIds.BAMBOO_JUNGLE_HILLS;
				} else if (i == 0) {
					l = BiomeIds.DEEP_OCEAN;
				} else if (i == BiomeIds.LUKEWARM_OCEAN) {
					l = BiomeIds.DEEP_LUKEWARM_OCEAN;
				} else if (i == BiomeIds.COLD_OCEAN) {
					l = BiomeIds.DEEP_COLD_OCEAN;
				} else if (i == BiomeIds.FROZEN_OCEAN) {
					l = BiomeIds.DEEP_FROZEN_OCEAN;
				} else if (i == BiomeIds.MOUNTAINS) {
					l = BiomeIds.WOODED_MOUNTAINS;
				} else if (i == BiomeIds.SAVANNA) {
					l = BiomeIds.SAVANNA_PLATEAU;
				} else if (BiomeLayers.areSimilar(i, BiomeIds.WOODED_BADLANDS_PLATEAU)) {
					l = BiomeIds.BADLANDS;
				} else if ((i == BiomeIds.DEEP_OCEAN || i == BiomeIds.DEEP_LUKEWARM_OCEAN || i == BiomeIds.DEEP_COLD_OCEAN || i == BiomeIds.DEEP_FROZEN_OCEAN)
					&& context.nextInt(3) == 0) {
					l = context.nextInt(2) == 0 ? BiomeIds.PLAINS : BiomeIds.FOREST;
				}

				if (k == 0 && l != i) {
					l = MUTATED_BIOMES.getOrDefault(l, i);
				}

				if (l != i) {
					int m = 0;
					if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 1), this.transformZ(z + 0)), i)) {
						m++;
					}

					if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 2), this.transformZ(z + 1)), i)) {
						m++;
					}

					if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 0), this.transformZ(z + 1)), i)) {
						m++;
					}

					if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 1), this.transformZ(z + 2)), i)) {
						m++;
					}

					if (m >= 3) {
						return l;
					}
				}
			}

			return i;
		}
	}
}
