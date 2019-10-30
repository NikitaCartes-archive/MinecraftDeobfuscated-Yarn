package net.minecraft.world.biome.layer;

public interface InitLayer {
	default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context) {
		return () -> context.createSampler((x, z) -> {
				context.initSeed((long)x, (long)z);
				return this.sample(context, x, z);
			});
	}

	int sample(LayerRandomnessSource context, int x, int y);
}
