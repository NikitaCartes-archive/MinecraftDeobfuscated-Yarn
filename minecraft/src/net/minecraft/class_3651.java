package net.minecraft;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.CrossSamplingLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

public enum class_3651 implements CrossSamplingLayer {
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
