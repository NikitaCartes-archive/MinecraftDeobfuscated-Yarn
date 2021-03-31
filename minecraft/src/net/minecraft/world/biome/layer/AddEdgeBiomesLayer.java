package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddEdgeBiomesLayer implements CrossSamplingLayer {
	INSTANCE;

	private static final IntSet SNOWY_IDS = new IntOpenHashSet(new int[]{26, 11, 12, 13, 140, 30, 31, 158, 10});
	private static final IntSet FOREST_IDS = new IntOpenHashSet(new int[]{168, 169, 21, 22, 23, 149, 151});

	@Override
	public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
		if (center == BiomeIds.MUSHROOM_FIELDS) {
			if (BiomeLayers.isShallowOcean(n) || BiomeLayers.isShallowOcean(e) || BiomeLayers.isShallowOcean(s) || BiomeLayers.isShallowOcean(w)) {
				return 15;
			}
		} else if (FOREST_IDS.contains(center)) {
			if (!isWooded(n) || !isWooded(e) || !isWooded(s) || !isWooded(w)) {
				return 23;
			}

			if (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w)) {
				return 16;
			}
		} else if (center != BiomeIds.MOUNTAINS && center != BiomeIds.WOODED_MOUNTAINS && center != BiomeIds.MOUNTAIN_EDGE) {
			if (SNOWY_IDS.contains(center)) {
				if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
					return 26;
				}
			} else if (center != BiomeIds.BADLANDS && center != BiomeIds.WOODED_BADLANDS_PLATEAU) {
				if (!BiomeLayers.isOcean(center)
					&& center != BiomeIds.RIVER
					&& center != BiomeIds.SWAMP
					&& (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
					return 16;
				}
			} else if (!BiomeLayers.isOcean(n)
				&& !BiomeLayers.isOcean(e)
				&& !BiomeLayers.isOcean(s)
				&& !BiomeLayers.isOcean(w)
				&& (!this.isBadlands(n) || !this.isBadlands(e) || !this.isBadlands(s) || !this.isBadlands(w))) {
				return 2;
			}
		} else if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
			return 25;
		}

		return center;
	}

	private static boolean isWooded(int id) {
		return FOREST_IDS.contains(id) || id == BiomeIds.FOREST || id == BiomeIds.TAIGA || BiomeLayers.isOcean(id);
	}

	private boolean isBadlands(int id) {
		return id == 37 || id == 38 || id == 39 || id == 165 || id == 166 || id == 167;
	}
}
