package net.minecraft.world.biome.layer;

public interface ParentedLayer extends CoordinateTransformer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context, LayerFactory<R> parent) {
		return () -> {
			R layerSampler = parent.make();
			return context.createSampler((i, j) -> {
				context.initSeed((long)i, (long)j);
				return this.sample(context, layerSampler, i, j);
			}, layerSampler);
		};
	}

	int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z);
}
