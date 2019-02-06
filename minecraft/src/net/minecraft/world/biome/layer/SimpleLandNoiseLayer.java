package net.minecraft.world.biome.layer;

public enum SimpleLandNoiseLayer implements IdentitySamplingLayer {
	field_16157;

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
		return BiomeLayers.isShallowOcean(i) ? i : layerRandomnessSource.nextInt(299999) + 2;
	}
}
