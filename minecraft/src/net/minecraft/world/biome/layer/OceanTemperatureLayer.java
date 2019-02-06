package net.minecraft.world.biome.layer;

import net.minecraft.util.math.noise.PerlinNoiseSampler;

public enum OceanTemperatureLayer implements InitLayer {
	field_16105;

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j) {
		PerlinNoiseSampler perlinNoiseSampler = layerRandomnessSource.getNoiseSampler();
		double d = perlinNoiseSampler.sample((double)i / 8.0, (double)j / 8.0, 0.0, 0.0, 0.0);
		if (d > 0.4) {
			return BiomeLayers.WARM_OCEAN_ID;
		} else if (d > 0.2) {
			return BiomeLayers.LUKEWARM_OCEAN_ID;
		} else if (d < -0.4) {
			return BiomeLayers.FROZEN_OCEAN_ID;
		} else {
			return d < -0.2 ? BiomeLayers.COLD_OCEAN_ID : BiomeLayers.OCEAN_ID;
		}
	}
}
