package net.minecraft;

import net.minecraft.world.biome.layer.BiomeLayers;

public enum class_3636 implements class_3663 {
	field_16052;

	@Override
	public int sample(class_3630 arg, int i, int j, int k, int l, int m) {
		if (BiomeLayers.isShallowOcean(m)) {
			int n = 0;
			if (BiomeLayers.isShallowOcean(i)) {
				n++;
			}

			if (BiomeLayers.isShallowOcean(j)) {
				n++;
			}

			if (BiomeLayers.isShallowOcean(l)) {
				n++;
			}

			if (BiomeLayers.isShallowOcean(k)) {
				n++;
			}

			if (n > 3) {
				if (m == BiomeLayers.WARM_OCEAN_ID) {
					return BiomeLayers.DEEP_WARM_OCEAN_ID;
				}

				if (m == BiomeLayers.LUKEWARM_OCEAN_ID) {
					return BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
				}

				if (m == BiomeLayers.OCEAN_ID) {
					return BiomeLayers.DEEP_OCEAN_ID;
				}

				if (m == BiomeLayers.COLD_OCEAN_ID) {
					return BiomeLayers.DEEP_COLD_OCEAN_ID;
				}

				if (m == BiomeLayers.FROZEN_OCEAN_ID) {
					return BiomeLayers.DEEP_FROZEN_OCEAN_ID;
				}

				return BiomeLayers.DEEP_OCEAN_ID;
			}
		}

		return m;
	}
}
