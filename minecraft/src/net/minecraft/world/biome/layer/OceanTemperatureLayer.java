package net.minecraft.world.biome.layer;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.BiomeLayers;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum OceanTemperatureLayer implements InitLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int x, int y) {
		PerlinNoiseSampler perlinNoiseSampler = context.getNoiseSampler();
		double d = perlinNoiseSampler.sample((double)x / 8.0, (double)y / 8.0, 0.0, 0.0, 0.0);
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
