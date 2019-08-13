package net.minecraft.world.biome.layer;

public enum AddIslandLayer implements CrossSamplingLayer {
	field_16158;

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
		return BiomeLayers.isShallowOcean(m)
				&& BiomeLayers.isShallowOcean(i)
				&& BiomeLayers.isShallowOcean(j)
				&& BiomeLayers.isShallowOcean(l)
				&& BiomeLayers.isShallowOcean(k)
				&& layerRandomnessSource.nextInt(2) == 0
			? 1
			: m;
	}
}
