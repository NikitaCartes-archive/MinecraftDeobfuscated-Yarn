package net.minecraft.world.biome.layer;

import net.minecraft.class_3630;
import net.minecraft.util.math.CoordinateTransformer;

public interface MergingLayer extends CoordinateTransformer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> layerSampleContext, LayerFactory<R> layerFactory, LayerFactory<R> layerFactory2) {
		return () -> {
			R layerSampler = layerFactory.make();
			R layerSampler2 = layerFactory2.make();
			return layerSampleContext.createSampler((i, j) -> {
				layerSampleContext.initSeed((long)i, (long)j);
				return this.sample(layerSampleContext, layerSampler, layerSampler2, i, j);
			}, layerSampler, layerSampler2);
		};
	}

	int sample(class_3630 arg, LayerSampler layerSampler, LayerSampler layerSampler2, int i, int j);
}
