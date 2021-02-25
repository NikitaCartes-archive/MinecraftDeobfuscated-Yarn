package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum EaseBiomeEdgeLayer implements CrossSamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
		int[] is = new int[1];
		if (!this.isMountainBiome(is, center)
			&& !this.areEdgesSimilar(is, n, e, s, w, center, 38, 37)
			&& !this.areEdgesSimilar(is, n, e, s, w, center, 39, 37)
			&& !this.areEdgesSimilar(is, n, e, s, w, center, 32, 5)) {
			if (center != 2 || n != 12 && e != 12 && w != 12 && s != 12) {
				if (center == 6) {
					if (n == 2 || e == 2 || w == 2 || s == 2 || n == 30 || e == 30 || w == 30 || s == 30 || n == 12 || e == 12 || w == 12 || s == 12) {
						return 1;
					}

					if (n == 21 || s == 21 || e == 21 || w == 21 || n == 168 || s == 168 || e == 168 || w == 168) {
						return 23;
					}
				}

				return center;
			} else {
				return 34;
			}
		} else {
			return is[0];
		}
	}

	private boolean isMountainBiome(int[] ids, int id) {
		if (!BiomeLayers.areSimilar(id, 3)) {
			return false;
		} else {
			ids[0] = id;
			return true;
		}
	}

	private boolean areEdgesSimilar(int[] ids, int n, int e, int s, int w, int center, int id1, int id2) {
		if (center != id1) {
			return false;
		} else {
			if (BiomeLayers.areSimilar(n, id1) && BiomeLayers.areSimilar(e, id1) && BiomeLayers.areSimilar(w, id1) && BiomeLayers.areSimilar(s, id1)) {
				ids[0] = center;
			} else {
				ids[0] = id2;
			}

			return true;
		}
	}
}
