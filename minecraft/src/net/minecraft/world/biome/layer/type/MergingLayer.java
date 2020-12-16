package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface MergingLayer extends CoordinateTransformer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context, LayerFactory<R> layer1, LayerFactory<R> layer2) {
		return () -> {
			R layerSampler = layer1.make();
			R layerSampler2 = layer2.make();
			return context.createSampler((x, z) -> {
				context.initSeed((long)x, (long)z);
				return this.sample(context, layerSampler, layerSampler2, x, z);
			}, layerSampler, layerSampler2);
		};
	}

	int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z);
}
