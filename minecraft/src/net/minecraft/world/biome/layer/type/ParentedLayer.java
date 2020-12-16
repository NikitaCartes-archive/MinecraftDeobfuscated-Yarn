package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface ParentedLayer extends CoordinateTransformer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context, LayerFactory<R> parent) {
		return () -> {
			R layerSampler = parent.make();
			return context.createSampler((x, z) -> {
				context.initSeed((long)x, (long)z);
				return this.sample(context, layerSampler, x, z);
			}, layerSampler);
		};
	}

	int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z);
}
