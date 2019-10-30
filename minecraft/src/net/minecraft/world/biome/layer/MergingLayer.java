package net.minecraft.world.biome.layer;

public interface MergingLayer extends CoordinateTransformer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context, LayerFactory<R> layer1, LayerFactory<R> layer2) {
		return () -> {
			R layerSampler = layer1.make();
			R layerSampler2 = layer2.make();
			return context.createSampler((i, j) -> {
				context.initSeed((long)i, (long)j);
				return this.sample(context, layerSampler, layerSampler2, i, j);
			}, layerSampler, layerSampler2);
		};
	}

	int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z);
}
