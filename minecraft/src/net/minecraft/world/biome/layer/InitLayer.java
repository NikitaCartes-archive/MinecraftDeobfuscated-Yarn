package net.minecraft.world.biome.layer;

public interface InitLayer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> layerSampleContext) {
		return () -> layerSampleContext.createSampler((i, j) -> {
				layerSampleContext.initSeed((long)i, (long)j);
				return this.sample(layerSampleContext, i, j);
			});
	}

	int sample(LayerRandomnessSource layerRandomnessSource, int i, int j);
}
