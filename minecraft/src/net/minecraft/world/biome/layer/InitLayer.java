package net.minecraft.world.biome.layer;

import net.minecraft.class_3630;

public interface InitLayer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> layerSampleContext) {
		return () -> layerSampleContext.createSampler((i, j) -> {
				layerSampleContext.initSeed((long)i, (long)j);
				return this.sample(layerSampleContext, i, j);
			});
	}

	int sample(class_3630 arg, int i, int j);
}
