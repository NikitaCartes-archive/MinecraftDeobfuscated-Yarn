package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum EaseBiomeEdgeLayer implements CrossSamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
		int[] is = new int[1];
		if (!this.method_15841(is, center)
			&& !this.method_15840(is, n, e, s, w, center, 38, 37)
			&& !this.method_15840(is, n, e, s, w, center, 39, 37)
			&& !this.method_15840(is, n, e, s, w, center, 32, 5)) {
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

	private boolean method_15841(int[] is, int i) {
		if (!BiomeLayers.areSimilar(i, 3)) {
			return false;
		} else {
			is[0] = i;
			return true;
		}
	}

	private boolean method_15840(int[] is, int i, int j, int k, int l, int m, int n, int o) {
		if (m != n) {
			return false;
		} else {
			if (BiomeLayers.areSimilar(i, n) && BiomeLayers.areSimilar(j, n) && BiomeLayers.areSimilar(l, n) && BiomeLayers.areSimilar(k, n)) {
				is[0] = m;
			} else {
				is[0] = o;
			}

			return true;
		}
	}
}
