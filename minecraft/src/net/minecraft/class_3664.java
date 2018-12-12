package net.minecraft;

import net.minecraft.util.math.NorthWestCoordinateTransformer;
import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;
import net.minecraft.world.biome.layer.ParentedLayer;

public interface class_3664 extends ParentedLayer, NorthWestCoordinateTransformer {
	int sample(class_3630 arg, int i);

	@Override
	default int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
		int k = layerSampler.sample(this.transformX(i + 1), this.transformY(j + 1));
		return this.sample(layerSampleContext, k);
	}
}
