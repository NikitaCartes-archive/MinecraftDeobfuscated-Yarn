package net.minecraft;

import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;
import net.minecraft.world.biome.layer.ParentedLayer;
import net.minecraft.world.biome.layer.VoidCoordinateTransformer;

public interface class_3661 extends ParentedLayer, VoidCoordinateTransformer {
	int method_15866(class_3630 arg, int i);

	@Override
	default int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
		return this.method_15866(layerSampleContext, layerSampler.sample(this.transformX(i), this.transformY(j)));
	}
}
