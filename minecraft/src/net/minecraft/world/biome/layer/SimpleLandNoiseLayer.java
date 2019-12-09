package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum SimpleLandNoiseLayer implements IdentitySamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int value) {
		return BiomeLayers.isShallowOcean(value) ? value : context.nextInt(299999) + 2;
	}
}
