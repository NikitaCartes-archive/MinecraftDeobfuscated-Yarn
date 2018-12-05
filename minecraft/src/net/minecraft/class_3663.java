package net.minecraft;

import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;
import net.minecraft.world.biome.layer.ParentedLayer;

public interface class_3663 extends ParentedLayer, class_3739 {
	int sample(class_3630 arg, int i, int j, int k, int l, int m);

	@Override
	default int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
		return this.sample(
			layerSampleContext,
			layerSampler.sample(this.transformX(i + 1), this.transformY(j + 0)),
			layerSampler.sample(this.transformX(i + 2), this.transformY(j + 1)),
			layerSampler.sample(this.transformX(i + 1), this.transformY(j + 2)),
			layerSampler.sample(this.transformX(i + 0), this.transformY(j + 1)),
			layerSampler.sample(this.transformX(i + 1), this.transformY(j + 1))
		);
	}
}
