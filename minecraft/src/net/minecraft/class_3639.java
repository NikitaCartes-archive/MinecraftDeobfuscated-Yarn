package net.minecraft;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.SouthEastSamplingLayer;

public enum class_3639 implements SouthEastSamplingLayer {
	field_16059;

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
		if (BiomeLayers.isShallowOcean(i)) {
			return i;
		} else {
			int j = layerRandomnessSource.nextInt(6);
			if (j == 0) {
				return 4;
			} else {
				return j == 1 ? 3 : 1;
			}
		}
	}
}
