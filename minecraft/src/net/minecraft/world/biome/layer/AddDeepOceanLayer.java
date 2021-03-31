package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddDeepOceanLayer implements CrossSamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
		if (BiomeLayers.isShallowOcean(center)) {
			int i = 0;
			if (BiomeLayers.isShallowOcean(n)) {
				i++;
			}

			if (BiomeLayers.isShallowOcean(e)) {
				i++;
			}

			if (BiomeLayers.isShallowOcean(w)) {
				i++;
			}

			if (BiomeLayers.isShallowOcean(s)) {
				i++;
			}

			if (i > 3) {
				if (center == BiomeIds.WARM_OCEAN) {
					return 47;
				}

				if (center == BiomeIds.LUKEWARM_OCEAN) {
					return 48;
				}

				if (center == 0) {
					return 24;
				}

				if (center == BiomeIds.COLD_OCEAN) {
					return 49;
				}

				if (center == BiomeIds.FROZEN_OCEAN) {
					return 50;
				}

				return 24;
			}
		}

		return center;
	}
}
