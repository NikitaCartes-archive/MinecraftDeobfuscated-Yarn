package net.minecraft.world.biome.layer;

public enum SimpleLandNoiseLayer implements IdentitySamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int value) {
		return BiomeLayers.isShallowOcean(value) ? value : context.nextInt(299999) + 2;
	}
}
