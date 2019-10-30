package net.minecraft.world.biome.layer;

public interface IdentitySamplingLayer extends ParentedLayer, IdentityCoordinateTransformer {
	int sample(LayerRandomnessSource context, int value);

	@Override
	default int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
		return this.sample(context, parent.sample(this.transformX(x), this.transformZ(z)));
	}
}
