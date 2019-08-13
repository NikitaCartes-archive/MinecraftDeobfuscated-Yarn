package net.minecraft.world.biome.layer;

public interface ParentedLayer extends CoordinateTransformer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> layerSampleContext, LayerFactory<R> layerFactory) {
		return () -> {
			R layerSampler = layerFactory.make();
			return layerSampleContext.createSampler((i, j) -> {
				layerSampleContext.initSeed((long)i, (long)j);
				return this.sample(layerSampleContext, layerSampler, i, j);
			}, layerSampler);
		};
	}

	int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j);
}
