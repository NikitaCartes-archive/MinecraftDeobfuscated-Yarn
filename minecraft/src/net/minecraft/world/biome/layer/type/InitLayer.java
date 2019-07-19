package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface InitLayer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context) {
		return () -> context.createSampler((x, z) -> {
				context.initSeed((long)x, (long)z);
				return this.sample(context, x, z);
			});
	}

	int sample(LayerRandomnessSource context, int x, int y);
}
