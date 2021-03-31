package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum ApplyOceanTemperatureLayer implements MergingLayer, IdentityCoordinateTransformer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
		int i = sampler1.sample(this.transformX(x), this.transformZ(z));
		int j = sampler2.sample(this.transformX(x), this.transformZ(z));
		if (!BiomeLayers.isOcean(i)) {
			return i;
		} else {
			int k = 8;
			int l = 4;

			for (int m = -8; m <= 8; m += 4) {
				for (int n = -8; n <= 8; n += 4) {
					int o = sampler1.sample(this.transformX(x + m), this.transformZ(z + n));
					if (!BiomeLayers.isOcean(o)) {
						if (j == BiomeIds.WARM_OCEAN) {
							return BiomeIds.LUKEWARM_OCEAN;
						}

						if (j == BiomeIds.FROZEN_OCEAN) {
							return BiomeIds.COLD_OCEAN;
						}
					}
				}
			}

			if (i == BiomeIds.DEEP_OCEAN) {
				if (j == BiomeIds.LUKEWARM_OCEAN) {
					return BiomeIds.DEEP_LUKEWARM_OCEAN;
				}

				if (j == 0) {
					return BiomeIds.DEEP_OCEAN;
				}

				if (j == BiomeIds.COLD_OCEAN) {
					return BiomeIds.DEEP_COLD_OCEAN;
				}

				if (j == BiomeIds.FROZEN_OCEAN) {
					return BiomeIds.DEEP_FROZEN_OCEAN;
				}
			}

			return j;
		}
	}
}
