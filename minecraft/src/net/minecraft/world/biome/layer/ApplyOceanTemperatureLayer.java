package net.minecraft.world.biome.layer;

import net.minecraft.class_3630;

public enum ApplyOceanTemperatureLayer implements MergingLayer, VoidCoordinateTransformer {
	field_16121;

	@Override
	public int sample(class_3630 arg, LayerSampler layerSampler, LayerSampler layerSampler2, int i, int j) {
		int k = layerSampler.sample(this.transformX(i), this.transformY(j));
		int l = layerSampler2.sample(this.transformX(i), this.transformY(j));
		if (!BiomeLayers.isOcean(k)) {
			return k;
		} else {
			int m = 8;
			int n = 4;

			for (int o = -8; o <= 8; o += 4) {
				for (int p = -8; p <= 8; p += 4) {
					int q = layerSampler.sample(this.transformX(i + o), this.transformY(j + p));
					if (!BiomeLayers.isOcean(q)) {
						if (l == BiomeLayers.WARM_OCEAN_ID) {
							return BiomeLayers.LUKEWARM_OCEAN_ID;
						}

						if (l == BiomeLayers.FROZEN_OCEAN_ID) {
							return BiomeLayers.COLD_OCEAN_ID;
						}
					}
				}
			}

			if (k == BiomeLayers.DEEP_OCEAN_ID) {
				if (l == BiomeLayers.LUKEWARM_OCEAN_ID) {
					return BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
				}

				if (l == BiomeLayers.OCEAN_ID) {
					return BiomeLayers.DEEP_OCEAN_ID;
				}

				if (l == BiomeLayers.COLD_OCEAN_ID) {
					return BiomeLayers.DEEP_COLD_OCEAN_ID;
				}

				if (l == BiomeLayers.FROZEN_OCEAN_ID) {
					return BiomeLayers.DEEP_FROZEN_OCEAN_ID;
				}
			}

			return l;
		}
	}
}
